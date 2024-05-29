package sbu.cs.Client;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private String username;
    private static final int PORT = 12345;


    public Client(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        } catch (IOException e) {
            closeEverything(socket , bufferedReader , bufferedWriter);
        }
    }


    public void enterGroupchat() {
        Scanner scanner = new Scanner(System.in);
        String username = scanner.nextLine();
        this.username = username;

        sendRequest(1);

        this.listenMessage();
        this.sendMessage();
    }

    public void sendMessage() {
        try {
            bufferedWriter.write(this.username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String message = username + " : " + scanner.nextLine();
                bufferedWriter.write(message);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }

        } catch (IOException e) {
            closeEverything(socket , bufferedReader , bufferedWriter);
        }
    }

    public void listenMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String message;

                while (socket.isConnected()) {
                    try {
                        message = bufferedReader.readLine();
                        System.out.println(message);
                    } catch (IOException e) {
                        closeEverything(socket , bufferedReader , bufferedWriter);
                    }
                }
            }
        }).start();
    }

    public void closeEverything(Socket socket , BufferedReader bufferedReader , BufferedWriter bufferedWriter) {
        try {
            if (socket != null) {
                socket.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void downloadFile() {
        Scanner scanner = new Scanner(System.in);

        sendRequest(1);

        ArrayList<String> fileNames =  getFileNames();

        System.out.println("Please choose one of the above files : ");

        for (int i = 0 ; i < fileNames.size() ; i++) {
            System.out.println(i + 1 + ") " + fileNames.get(i));
        }
        System.out.print(": ");

        int option = scanner.nextInt();

        while (true) {
            if (option > 0 && option <= fileNames.size()) {
                break;
            } else {
                System.out.print("Please choose between 1 and " + fileNames.size() + " : ");
                option = scanner.nextInt();
            }
        }

        sendRequest(option);
        getFile();
    }


    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        Socket socket = new Socket("localhost" , PORT);
        Client client = new Client(socket);
        int option;

        System.out.print("1) Enter group chat\n2) File download\n: ");
        option = scanner.nextInt();

        while (true) {
            if (option == 1 | option == 2) {
                break;
            } else {
                System.out.print("Please choose one of the above (1 / 2) : ");
                option = scanner.nextInt();
            }
        }

        switch (option) {
            case 1 :
                client.enterGroupchat();
                break;

            case 2 :
                client.downloadFile();
                break;
        }
    }
}