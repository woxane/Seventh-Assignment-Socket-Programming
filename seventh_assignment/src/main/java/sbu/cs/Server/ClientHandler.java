package sbu.cs.Server;

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
}