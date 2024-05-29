package sbu.cs.Server;

import sbu.cs.Client.Client;

import java.io.*;
import java.net.Socket;
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
        sendMenu();
        int index = getSelectedOption();

        switch (index) {
            // Enter group chat
            case 1 :
                enterGroupchat();
                break;

            // Download file
            case 2 :
                downloadFile();
                break;
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
}