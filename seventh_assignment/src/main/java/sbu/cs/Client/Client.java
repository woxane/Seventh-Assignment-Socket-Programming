package sbu.cs.Client;


import java.io.*;
import java.net.Socket;
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


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
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
                enterGroupchat();
                break;

            case 2 :
                downloadFile();
                break;
        }
    }
}