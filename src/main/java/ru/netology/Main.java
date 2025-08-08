package ru.netology;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(9999)) {
            System.out.println("Server started on port 9999...");
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(() -> {
                    try {
                        Request request = new Request(socket.getInputStream());
                        System.out.println("Path: " + request.getPath());
                        System.out.println("Query params: " + request.getQueryParams());
                        System.out.println("Post params: " + request.getPostParams());

                        String response = "HTTP/1.1 200 OK\r\n\r\nHello from server!";
                        socket.getOutputStream().write(response.getBytes());
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        }
    }
}
