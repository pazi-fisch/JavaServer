package org.example;

import org.example.server.Server;

public class Main {

    public static void main(String[] args) {
        try {
            System.out.println("starting server ....");
            Server server = new Server();
            server.startServer();
            System.out.println("server running on: http://localhost:8000/api/notes");
            System.out.println("press <enter> to stop");
            System.in.read();
            System.out.println("stopping server ....");
            server.stopServer();
            System.out.println("server stopped, bye-bye");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}