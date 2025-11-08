package org.example;

import org.example.server.Server;

public class Main {

    /**
     * Main entry point of the program to launch it.
     * Starts a server, prints some pretty info and waits for user input before stopping.
     */
    public static void main(String[] args) {
        try {
            System.out.println("\n\n\n");
            System.out.println("############################");
            System.out.println("# SIMPLE NOTES REST SERVER #");
            System.out.println("############################\n");
            // create and start the server
            System.out.println("starting server ....");
            Server server = new Server();
            server.startServer();
            System.out.println("server running on: http://localhost:8000/api/notes\n");
            // await input from user, then stop the server
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