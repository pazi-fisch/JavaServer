package org.example.db;

import java.sql.*;
import java.util.ArrayList;

/**
 * Handles connection/access to the DB.
 * To open and gain access to the DB call <code>Database.getInstance()</code> first.
 * When done, disconnect/close the DB through <code>Database.closeDB()</code>.
 */
public class Database {

    // name of our DB, in relative path
    private static final String DATABASE = "jdbc:sqlite:NotesDB";

    // structure of "Note" table to store notes (duh!)
    private static final String NOTE_TABLE = "Note";
    private static final String NOTE_ID_FIELD = "n_id";
    private static final String NOTE_TITLE_FIELD = "n_title";
    private static final String NOTE_CONTENT_FIELD = "n_content";
    private static final String NOTE_TIMESTAMP_FIELD = "n_timestamp";
    private static final String CREATE_NOTE_TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS " + NOTE_TABLE +
            " (" +
                Database.NOTE_ID_FIELD + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                Database.NOTE_TITLE_FIELD + " TEXT NOT NULL, " +
                Database.NOTE_CONTENT_FIELD + " TEXT, " +
                Database.NOTE_TIMESTAMP_FIELD + " INTEGER NOT NULL" +
            ");";

    // DB operations handled through a singleton
    private static Database instance = null;

    /**
     * Provides an instance of the DB to access it and manipulate data.
     * Returns already open connection, if none found a new connection is created.
     */
    public static Database getInstance() throws Exception{
        if (Database.instance == null) {
            Database.instance = new Database();
        }
        return Database.instance;
    }

    // actual connection to the DB
    private Connection connection = null;


    /**
     * Open a connection to the DB and create it (if not already existing).
     */
    private Database() throws Exception {
        // create a new connection to the DB
        this.connection = DriverManager.getConnection(Database.DATABASE);
        // create the DB/table
        Statement statement = this.connection.createStatement();
        statement.execute(Database.CREATE_NOTE_TABLE_SQL);
        statement.close();
    }

    /**
     * Parse a single (first) ResultSet from query to Note.
     */
    private Note resultSetToNote(ResultSet resultSet) throws Exception {
        Note note = null;
        if (resultSet != null && resultSet.next()) {
            // ResultSet found, parse
            note = new Note(
                    resultSet.getInt(Database.NOTE_ID_FIELD),
                    resultSet.getString(Database.NOTE_TITLE_FIELD),
                    resultSet.getString(Database.NOTE_CONTENT_FIELD),
                    resultSet.getLong(Database.NOTE_TIMESTAMP_FIELD));
        }
        return note;
    }

    /**
     * Parse a ResultSet from query to list of Notes.
     */
    private ArrayList<Note> resultSetToNoteList(ResultSet resultSet) throws Exception {
        ArrayList<Note> notes = new ArrayList<Note>();
        // loop and parse notes one by one
        Note note = null;
        while ((note = this.resultSetToNote(resultSet)) != null) {
            notes.add(note);
        }
        return notes;
    }

    /**
     * Add a new Note to the DB, with current timestamp.
     */
    public void addNote(Note note) throws Exception {
        if (note != null && note.isValid()) {
            // Note valid, update its timestamp to now
            note.setTimestampNow();
            // insert given Note to the DB
            String sql =
                    "INSERT INTO " + Database.NOTE_TABLE +
                            "(" + Database.NOTE_TITLE_FIELD + ", " + Database.NOTE_CONTENT_FIELD + ", " + Database.NOTE_TIMESTAMP_FIELD + ") " +
                            "VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setString(1, note.getTitle());
            preparedStatement.setString(2, note.getContent());
            preparedStatement.setLong(3, note.getTimestamp());
            preparedStatement.executeUpdate();
            // done, close
            preparedStatement.close();
        }
    }

    /**
     * Delete given Note from DB.
     */
    public void deleteNote(Note note) throws Exception {
        if (note != null) {
            this.deleteNote(note.getID());
        }
    }

    /**
     * Delete Note with given ID from DB.
     */
    public void deleteNote(int id) throws Exception {
        // delete given Note form DB
        String sql = "DELETE FROM " + Database.NOTE_TABLE + " WHERE " + Database.NOTE_ID_FIELD + " = ?";
        PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
        // done, close
        preparedStatement.close();
    }

    /**
     * Retrieve Note with given ID from DB.
     */
    public Note getNote(int id) throws Exception {
        Note note = null;
        // retrieve Note with given ID from DB
        String sql = "SELECT * FROM " + Database.NOTE_TABLE + " WHERE " + Database.NOTE_ID_FIELD + " = ?";
        Statement statement = this.connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        // parse found ResultSet to Note
        note = this.resultSetToNote(resultSet);
        // done, close
        resultSet.close();
        statement.close();
        return note;
    }

    /**
     * Retrieve all Notes from DB.
     */
    public ArrayList<Note> getAllNotes() throws Exception {
        ArrayList<Note> notes = null;
        // retrieve all Notes from DB
        String sql = "SELECT * FROM " + Database.NOTE_TABLE;
        Statement statement = this.connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        // parse found ResultSet to list of Notes
        notes = this.resultSetToNoteList(resultSet);
        // done, close
        resultSet.close();
        statement.close();
        return notes;
    }

    /**
     * Update given Note into the DB and update to current timestamp.
     */
    public void updateNote(Note note) throws Exception {
        if (note != null && note.isValid()) {
            // Note valid, update its timestamp to now
            note.setTimestampNow();
            // update given Note into the DB
            String sql =
                    "UPDATE " + Database.NOTE_TABLE + " SET " +
                            Database.NOTE_TITLE_FIELD + " = ?, " + Database.NOTE_CONTENT_FIELD + " = ?, " + Database.NOTE_TIMESTAMP_FIELD + " = ? " +
                            "WHERE " + Database.NOTE_ID_FIELD + " = ?";
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setString(1, note.getTitle());
            preparedStatement.setString(2, note.getContent());
            preparedStatement.setLong(3, note.getTimestamp());
            preparedStatement.setInt(4, note.getID());
            // done, close
            preparedStatement.executeUpdate();
            preparedStatement.close();
        }
    }

    /**
     * Close the connection/access to the DB.
     */
    public void closeDB() throws Exception {
        if (this.connection != null) {
            this.connection.close();
        }
        Database.instance = null;
    }

}
