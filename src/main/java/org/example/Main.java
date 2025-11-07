package org.example;

import com.sun.net.httpserver.HttpServer;

import java.io.OutputStream;
import java.net.InetSocketAddress;

public class Main {

    private static final int SERVER_PORT = 8000;

    public static void main(String[] args) throws Exception {
        System.out.println("starting");
        HttpServer server = HttpServer.create(new InetSocketAddress(Main.SERVER_PORT), 0);
        server.createContext("/api/notes", (exchange -> {
            if (exchange.getRequestMethod().equals("GET")) {
                String responseText = "Hello!";
                exchange.sendResponseHeaders(200, responseText.getBytes().length);
                OutputStream outputStream = exchange.getResponseBody();
                outputStream.write(responseText.getBytes());
                outputStream.flush();
            } else {
                exchange.sendResponseHeaders(405, -1); // 405 Method Not Allowed
            }
            exchange.close();
        }));
        server.setExecutor(null);
        server.start();
        System.out.println("running");
    }

}