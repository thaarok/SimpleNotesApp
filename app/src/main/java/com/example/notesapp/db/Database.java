package com.example.notesapp.db;

import androidx.room.RoomDatabase;

@androidx.room.Database(entities = {Note.class}, version = 1, exportSchema = false)
public abstract class Database extends RoomDatabase {

    public abstract NoteDao getNoteDao();

}
