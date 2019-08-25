package com.example.notesapp.db;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class NoteViewModel extends AndroidViewModel {

    private Database database;

    public NoteViewModel(Application application) {
        super(application);
        database = Database.getInstance(application.getApplicationContext());
    }

    public void insertAsync(final Note note) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                long id = database.getNoteDao().insert(note);
                System.out.println("INSERTED " + id);
                return null;
            }
        }.execute();
    }

    public void updateAsync(final Note note) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                database.getNoteDao().update(note);
                System.out.println("UPDATED");
                return null;
            }
        }.execute();
    }

    public void deleteAsync(final Note note) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                database.getNoteDao().delete(note);
                System.out.println("DELETED");
                return null;
            }
        }.execute();
    }

    public LiveData<Note> getById(long id) {
        return database.getNoteDao().getByIdAsync(id);
    }

    public LiveData<List<Note>> getAll() {
        return database.getNoteDao().getAllAsync();
    }

}
