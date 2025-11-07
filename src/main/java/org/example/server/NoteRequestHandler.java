package org.example.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.db.Database;
import org.example.db.Note;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Handler for the REST requests for the notes, supports GET, POST, PUT, DELETE.
 */
public class NoteRequestHandler implements HttpHandler {

    public static final String REQUEST_PATH = "/api/notes";

    // supported methods
    private static final String GET_REQUEST = "GET";
    private static final String POST_REQUEST = "POST";
    private static final String PUT_REQUEST = "PUT";
    private static final String DELETE_REQUEST = "DELETE";

    @Override
    public void handle(HttpExchange exchange) {
        String response = null;
        try {
            // parse the request
            switch (exchange.getRequestMethod()) {
                case NoteRequestHandler.GET_REQUEST:
                    // GET
                    this.handleGetRequest(exchange);
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
                    // unknown method
                    exchange.sendResponseHeaders(405, -1);
            }
            // done, close
            exchange.close();
        } catch (Exception e) {
            // FIXME something went wrong
            e.printStackTrace();
        }
    }

    private void handleGetRequest(HttpExchange exchange) throws Exception {
        JsonObject response = new JsonObject();
        // check the URI to parse the request: collection or singleton?
        String path = exchange.getRequestURI().getPath();
        if (path.contentEquals(NoteRequestHandler.REQUEST_PATH)) {
            // collection, get all notes
            ArrayList<Note> notes = Database.getInstance().getAllNotes();
            if (notes != null && notes.size() != 0) {
                // notes found, parse as JSON array
                JsonArray jsonArray = new JsonArray();
                for (Note note : notes) {
                    jsonArray.add(note.toJsonObject());
                }
                response = jsonArray.getAsJsonObject();
            }
        } else {
            // singleton, parse the ID and get the note
            int id = this.parseSingletonFromPath(path);
            Note note = Database.getInstance().getNote(id);
            if (note != null) {
                // note found, parse as JSON object
                response = note.toJsonObject();
            }
        }
        // reply with the retrieved data
        this.sendResponse(exchange, response.toString());
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

    /**
     * Parses the request body of an HttpExchange as JSON.
     */
    private JsonObject requestBodyToJSON(HttpExchange exchange) {
        JsonObject jsonObject = null;
        try {
            // try to parse the request body as JSON
            InputStreamReader inputStreamReader = new InputStreamReader(exchange.getRequestBody());
            jsonObject = JsonParser.parseReader(inputStreamReader).getAsJsonObject();
            // done, close
            inputStreamReader.close();
        } catch (Exception ignored) { ; }
        return jsonObject;
    }

    /**
     * Send the response for the exchange, with code 200 - OK.
     */
    private void sendResponse(HttpExchange exchange, String response) throws IOException {
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    /**
     * Parse the singleton from the given path.
     */
    private int parseSingletonFromPath(String path) {
        int singleton = -1;
        if (path != null && path.length() != 0) {
            // parse the singleton from the path
            // for simplicity assume the path is correctly formatted
            String[] pathParts = path.split("/");
            singleton = Integer.parseInt(pathParts[pathParts.length - 1]);
        }
        return singleton;
    }

}
