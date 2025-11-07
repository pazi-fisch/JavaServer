package org.example.db;

import com.google.gson.JsonObject;

import java.util.Date;

public class Note {

    public static final String JSON_ID_KEY = "id";
    public static final String JSON_TITLE_KEY = "title";
    public static final String JSON_CONTENT_KEY = "content";
    public static final String JSON_TIMESTAMP_KEY = "timestamp";

    private int id = -1;
    private String title = null;
    private String content = null;
    private long timestamp = -1;

    /**
     * Create a new Note with only title and content.
     * Intended for new notes which don't have an ID yet.
     */
    public Note(String title, String content) {
        this.title = title;
        this.content = content;
    }

    /**
     * Create a (full) Note with all its data.
     */
    public Note(int id, String title, String content, long timestamp) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
    }

    public int getID() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    /**
     * Set the timestamp of this Note to now.
     */
    public void setTimestampNow() {
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Get the timestamp of the Note, as Date.
     */
    public Date getTimestampAsDate() {
        Date date = new Date(this.timestamp);
        return date;
    }

    /**
     * Checks if this Note is valid (e.g. complies with DB).
     * A note is considered valid if it has a title which is not empty/blank.
     */
    public boolean isValid() {
        boolean isValid = false;
        if (this.title != null && !this.title.isBlank()) {
            isValid = true;
        }
        return isValid;
    }

    /**
     * Parse the data contained in this Note as JSONObject
     */
    public JsonObject toJsonObject() {
        JsonObject jsonNote = new JsonObject();
        // add each property one by one
        jsonNote.addProperty(Note.JSON_ID_KEY, this.id);
        jsonNote.addProperty(Note.JSON_TITLE_KEY, this.title);
        jsonNote.addProperty(Note.JSON_CONTENT_KEY, this.content);
        jsonNote.addProperty(Note.JSON_TIMESTAMP_KEY, this.timestamp);
        return jsonNote;
    }

}
