package com.example.notesapp.db;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.room.Room;

import java.util.List;

public class NoteRepository extends AndroidViewModel {

    private Database database;

    public NoteRepository(Application application) {
        super(application);
        database = Room.databaseBuilder(application.getApplicationContext(), Database.class, "DB_NOTES").build();
    }

    public void insert(final Note note) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                database.getNoteDao().insert(note);
                System.out.println("INSERTED");
                return null;
            }
        }.execute();
    }

    public void update(final Note note) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                database.getNoteDao().update(note);
                System.out.println("UPDATED");
                return null;
            }
        }.execute();
    }

    public void delete(final Note note) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                database.getNoteDao().delete(note);
                System.out.println("DELETED");
                return null;
            }
        }.execute();
    }

    public LiveData<Note> getById(int id) {
        return database.getNoteDao().getById(id);
    }

    public LiveData<List<Note>> getAll() {
        return database.getNoteDao().getAll();
    }

}
