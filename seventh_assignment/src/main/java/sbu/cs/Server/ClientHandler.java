package sbu.cs.Server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import sbu.cs.Client.Client;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private static List<String> chatHistory = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;
    private static final int HISTORY_SIZE = 10;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            clientHandlers.add(this);


        } catch (IOException e) {
            closeEverything(socket , bufferedReader , bufferedWriter);
        }

    }


    @Override
    public void run() {
        int index = getSelectedOption();

        switch (index) {
            // Enter group chat
            case 1 :
                enterGroupchat();
                break;

            // Download file
            case 2 :
                downloadFile();
                closeEverything(socket , bufferedReader , bufferedWriter);
                break;

            default :
                System.out.println("Wrong shit happend !!!!!");
        }
    }

    public void enterGroupchat() {
        try {
            String message;

            this.clientUsername = this.bufferedReader.readLine();
            historyBroadcast();
            broadcast("SERVER : " + this.clientUsername + " has entered the chat !");

            while (socket.isConnected()) {
                message = this.bufferedReader.readLine();
                broadcast(message);
            }

        } catch (IOException e) {
            closeEverything(socket , bufferedReader , bufferedWriter);
        }
    }

    public void updateChatHistory(String message) {
        chatHistory.add(message);
        if (chatHistory.size() > HISTORY_SIZE) {
            chatHistory.remove(0);
        }
    }

    public void broadcast(String message) {
        updateChatHistory(message);

        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (!clientHandler.clientUsername.equals(this.clientUsername)) {
                    clientHandler.bufferedWriter.write(message);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeEverything(socket , bufferedReader , bufferedWriter);
            }
        }
    }

    public void removeClientHandler() {
        clientHandlers.remove(this);
        broadcast("SERVER : " + this.clientUsername + " has left the chat !");
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();

        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void historyBroadcast() {
        try {
            if (chatHistory.size() != 0) {
                this.bufferedWriter.write("Last " + chatHistory.size() + " messages : ");
                this.bufferedWriter.newLine();
                this.bufferedWriter.flush();

                for (String message : chatHistory) {
                    this.bufferedWriter.write("\t" + message);
                    this.bufferedWriter.newLine();
                    this.bufferedWriter.flush();
                }
            }
        } catch (IOException e) {
            closeEverything(socket , bufferedReader , bufferedWriter);

        }
    }


    public int getSelectedOption() {
        try {
            int index = Integer.parseInt(bufferedReader.readLine());
            return index;
        } catch (IOException e) {
            closeEverything(socket , bufferedReader , bufferedWriter);
        }
        return 0;
    }

    public void downloadFile() {
        sendFileList();
        int index = getSelectedOption();
        sendSelectedFile(index);
    }

    public void sendFileList() {
        File[] fileLists = new File(Server.FILE_PATH).listFiles();
        ArrayList<String> fileNames = new ArrayList<>();

        for (File file : fileLists) {
            fileNames.add(file.getName());
        }

        Response response = new Response(fileNames);
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer();

        try {
            String json = objectWriter.writeValueAsString(response);
            bufferedWriter.write(json);
            bufferedWriter.newLine();
            bufferedWriter.flush();

        } catch (Exception e) {
            System.err.println("Error during deserialization: " + e.getMessage());
            e.printStackTrace();

            closeEverything(socket , bufferedReader , bufferedWriter);
        }
    }

    public void sendSelectedFile(int index) {
        try {
            File[] fileLists = new File(Server.FILE_PATH).listFiles();
            File selectedFile = fileLists[index];

            List<String> fileLines = Files.readAllLines(selectedFile.toPath());
            String fileContent = String.join("\n" , fileLines);

            try {
                bufferedWriter.write(fileContent);
                bufferedWriter.newLine();
                bufferedWriter.flush();

            } catch (IOException e) {
                closeEverything(socket , bufferedReader , bufferedWriter);
            }

        } catch (IOException e) {
            System.err.println("Error during read file ! : " + e.getMessage());
            e.printStackTrace();

            closeEverything(socket , bufferedReader , bufferedWriter);
        }
    }
}