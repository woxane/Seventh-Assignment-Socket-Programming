package sbu.cs.Client;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.*;
import java.net.Socket;
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

        Request request = new Request(1);
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer();

        try {
            String json = objectWriter.writeValueAsString(request);
            bufferedWriter.write(json);
            bufferedWriter.newLine();
            bufferedWriter.flush();

        } catch (JsonProcessingException e) {
            System.err.println("Error during serialize the class to json: " + e.getMessage());
            e.printStackTrace();

            closeEverything(socket , bufferedReader , bufferedWriter);

        } catch (IOException e) {
            System.err.println("Error during sending the serialized request");

            closeEverything(socket , bufferedReader , bufferedWriter);
        }

        this.listenMessage();
        this.sendMessage();
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