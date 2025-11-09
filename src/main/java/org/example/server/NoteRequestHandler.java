package org.example.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.db.Database;
import org.example.db.Note;

import java.io.*;
import java.net.HttpURLConnection;
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

    private static final String RESPONSE_SUCCESS = "success";
    private static final String RESPONSE_RESULT = "result";

    @Override
    public void handle(HttpExchange exchange) {
        // set CORS policy
        // without this clients won't be able to access
        Headers headers = exchange.getResponseHeaders();
        headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Methods", "GET, PUT, POST, DELETE");
        headers.add("Access-Control-Allow-Headers", "Content-Type");
        // parse and handle the request
        try {
            switch (exchange.getRequestMethod()) {
                case NoteRequestHandler.GET_REQUEST:
                    // GET
                    this.handleGetRequest(exchange);
                    break;
                case NoteRequestHandler.POST_REQUEST:
                    // POST
                    this.handlePostRequest(exchange);
                    break;
                case NoteRequestHandler.PUT_REQUEST:
                    // PUT
                    this.handlePutRequest(exchange);
                    break;
                case NoteRequestHandler.DELETE_REQUEST:
                    // DELETE
                    this.handleDeleteRequest(exchange);
                    break;
                default:
                    // unknown method
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, -1);
            }
        } catch (Exception e) {
            // something went wrong, respond with an error
            try {this.sendFailureResponse(exchange, e.getClass() + " : " + e.getMessage()); } catch (Exception ignored) { ; }
            e.printStackTrace();
        } finally {
            // done, close
            exchange.close();
        }
    }

    /**
     * Returns the desires note(s).
     * If on whole collection returns an array of Note objects.
     * If on a singleton returns the Note object itself.
     */
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
                response.add(NoteRequestHandler.RESPONSE_RESULT, jsonArray);
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
        this.sendSuccessResponse(exchange, response.toString());
    }

    /**
     * Adds the passed JSON data as new Note.
     */
    private void handlePostRequest(HttpExchange exchange) throws Exception {
        // parse the passed JSON as Note
        JsonObject jsonNote = this.requestBodyToJSON(exchange);
        Note note = new Note(jsonNote.get(Note.JSON_TITLE_KEY).getAsString(), jsonNote.get(Note.JSON_CONTENT_KEY).getAsString());
        // add it to the DB
        Database.getInstance().addNote(note);
        // reply with success
        this.sendSuccessResponse(exchange, NoteRequestHandler.RESPONSE_SUCCESS);
    }

    /**
     * Updates the specified singleton/Note with the passed JSON data.
     */
    private void handlePutRequest(HttpExchange exchange) throws Exception {
        // parse the ID from the path
        String path = exchange.getRequestURI().getPath();
        int id = this.parseSingletonFromPath(path);
        // parse the passed JSON as Note, with corresponding ID
        JsonObject jsonNote = this.requestBodyToJSON(exchange);
        Note note = new Note(id, jsonNote.get(Note.JSON_TITLE_KEY).getAsString(), jsonNote.get(Note.JSON_CONTENT_KEY).getAsString(), -1);
        // update the Note in the DB
        Database.getInstance().updateNote(note);
        // reply with success
        this.sendSuccessResponse(exchange, NoteRequestHandler.RESPONSE_SUCCESS);
    }

    /**
     * Deletes the specified singleton/Note.
     */
    private void handleDeleteRequest(HttpExchange exchange) throws Exception {
        // parse the ID from the path
        String path = exchange.getRequestURI().getPath();
        int id = this.parseSingletonFromPath(path);
        // delete the corresponding Note in the DB
        Database.getInstance().deleteNote(id);
        // reply with success
        this.sendSuccessResponse(exchange, NoteRequestHandler.RESPONSE_SUCCESS);
    }

    /**
     * Parses the request body of an HttpExchange as JSON.
     */
    private JsonObject requestBodyToJSON(HttpExchange exchange) throws Exception {
        JsonObject jsonObject = null;
        // try to parse the request body as JSON
        InputStreamReader inputStreamReader = new InputStreamReader(exchange.getRequestBody());
        jsonObject = JsonParser.parseReader(inputStreamReader).getAsJsonObject();
        // done, close
        inputStreamReader.close();
        return jsonObject;
    }

    /**
     * Send custom response/code for the exchange.
     */
    private void sendResponse(HttpExchange exchange, String response, int code) throws Exception {
        // append newline, if it doesn't end with one
        if (response != null && !response.endsWith("\n")) {
            response = response + "\n";
        }
        // send the provided response/code
        exchange.sendResponseHeaders(code, response.getBytes().length);
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    /**
     * Send successful response for the exchange, with code 200 - OK.
     */
    private void sendSuccessResponse(HttpExchange exchange, String response) throws Exception {
        this.sendResponse(exchange, response, HttpURLConnection.HTTP_OK);
    }

    /**
     * Send failure response for the exchange, with code 500 - internal error.
     */
    private void sendFailureResponse(HttpExchange exchange, String response) throws Exception {
        this.sendResponse(exchange, response, HttpURLConnection.HTTP_INTERNAL_ERROR);
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
