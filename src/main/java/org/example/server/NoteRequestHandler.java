package org.example.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Handler for the REST requests for the notes, supports GET, POST, PUT, DELETE.
 */
public class NoteRequestHandler implements HttpHandler {

    // supported methods
    private static final String GET_REQUEST = "GET";
    private static final String POST_REQUEST = "POST";
    private static final String PUT_REQUEST = "PUT";
    private static final String DELETE_REQUEST = "DELETE";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response = null;
        // parse the request
        switch (exchange.getRequestMethod()) {
            case NoteRequestHandler.GET_REQUEST:
                // GET
                response = this.handleGetRequest();
                this.sendResponse(exchange, response);
                break;
            case NoteRequestHandler.POST_REQUEST:
                // POST
                response = this.handlePostRequest();
                this.sendResponse(exchange, response);
                break;
            case NoteRequestHandler.PUT_REQUEST:
                // POST
                response = this.handlePutRequest();
                this.sendResponse(exchange, response);
                break;
            case NoteRequestHandler.DELETE_REQUEST:
                // DELETE
                response = this.handleDeleteRequest();
                this.sendResponse(exchange, response);
                break;
            default:
                // error, unknown method
                exchange.sendResponseHeaders(405, -1);
        }
        // done, close
        exchange.close();
    }

    private void sendResponse(HttpExchange exchange, String response) throws IOException {
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private String handleGetRequest() {
        String response = "GET";
        return response;
    }

    private String handlePostRequest() {
        String response = "POST";
        return response;
    }

    private String handlePutRequest() {
        String response = "PUT";
        return response;
    }

    private String handleDeleteRequest() {
        String response = "DELETE";
        return response;
    }

}
