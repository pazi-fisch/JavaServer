package org.example.server;

import com.sun.net.httpserver.HttpServer;
import org.example.db.Database;

import java.net.InetSocketAddress;

/**
 * Local server running on port 8000, which exposes the REST API to the Note DB.
 */
public class Server {

    // server port
    private static final int SERVER_PORT = 8000;

    private HttpServer server = null;

    /**
     * Create a new server instance, without starting it.
     * To start it call <code>startServer()</code>.
     * To stop it call <code>stopServer()</code>.
     */
    public Server() throws Exception {
        // create the server, with basic executor
        this.server = HttpServer.create(new InetSocketAddress(Server.SERVER_PORT), 0);
        this.server.setExecutor(null);
        // assign the handler to handle all note requests
        this.server.createContext(NoteRequestHandler.REQUEST_PATH, new NoteRequestHandler());
    }

    /**
     * Prepares the database for access and starts the server.
     */
    public void startServer() throws Exception {
        // open and connect to the DB
        Database.getInstance();
        // start the server
        this.server.start();
    }

    /**
     * Closes the DB access/connection and stops the server.
     */
    public void stopServer() throws Exception {
        // close the DB
        Database.getInstance().closeDB();
        // stop the server itself
        this.server.stop(0);
    }

}
