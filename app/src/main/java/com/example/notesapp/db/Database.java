package com.example.notesapp.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@androidx.room.Database(entities = {Note.class}, version = 2, exportSchema = false)
public abstract class Database extends RoomDatabase {

    public abstract NoteDao getNoteDao();

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Note ADD COLUMN deleted INTEGER NOT NULL DEFAULT 0");
        }
    };

    public static Database getInstance(Context context) {
        return Room.databaseBuilder(context, Database.class, "DB_NOTES")
                .addMigrations(MIGRATION_1_2)
                .build();
    }

}
