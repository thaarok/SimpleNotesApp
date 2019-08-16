package com.example.notesapp.db;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.List;

public class NoteRepository extends AndroidViewModel {

    private Database database;

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Note ADD COLUMN deleted INTEGER NOT NULL DEFAULT 0");
        }
    };

    public NoteRepository(Application application) {
        super(application);
        database = Room.databaseBuilder(application.getApplicationContext(), Database.class, "DB_NOTES")
                .addMigrations(MIGRATION_1_2)
                .build();
    }

    public void insert(final Note note) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                long id = database.getNoteDao().insert(note);
                System.out.println("INSERTED " + id);
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

    public LiveData<Note> getById(long id) {
        return database.getNoteDao().getById(id);
    }

    public LiveData<List<Note>> getAll() {
        return database.getNoteDao().getAll();
    }

}
