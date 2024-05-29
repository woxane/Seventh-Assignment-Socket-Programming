package sbu.cs.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static final String FILE_PATH = "./data/" ;
    private static final int PORT = 12345;
    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("A new client has connected !");

                ClientHandler clientHandler = new ClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();

            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void closeServer() {
        try {
            if (serverSocket != null) {
                serverSocket.close();

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server started on port : " + PORT);

        Server server = new Server(serverSocket);
        server.startServer();

    }
}