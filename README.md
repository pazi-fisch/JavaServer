
## JAVA SERVER

Simple server in Java with DB to hold notes, exposing a REST API.

When launched its REST API is accessible at: ```http://localhost:8000/api/notes```

## Structure of notes

Notes consist of an auto-generated unique ID, a mandatory title, optional content and an auto-generated "last modified" timestamp.

The timestamp is automatically assigned whenever its created/modified, to reflect when it was last modified.

## REST API through curl

[`JavascriptClient`](https://github.com/pazi-fisch/JavascriptClient) exposes a bare UI to visualize/manipulate the notes. 

Alternatively the server can also be queried through ```curl```, with notes exhanged in JSON.

- Get all notes: <br>
  ```curl -X GET localhost:8000/api/notes```
- Get a single note with specific ID: <br>
  ```curl -X GET localhost:8000/api/notes/{id}```
- Add a new note: <br>
  ```curl -X POST localhost:8000/api/notes -d "{\"title\" : \"Lorem Ipsum\" , \"content\" : \"Lorem ipsum dolor sit amet\"}"```
- Edit a note with specific ID: <br>
  ```curl -X PUT localhost:8000/api/notes/{id} -d "{\"title\" : \"De finibus bonorum et malorum\" , \"content\" : \"Sed ut perspiciatis\"}"```
- Delete a note with specific ID: <br>
  ```curl -X DELETE localhost:8000/api/notes/{id}```
