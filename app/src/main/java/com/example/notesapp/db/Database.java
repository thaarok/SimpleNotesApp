package com.example.notesapp.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.Date;

@androidx.room.Database(entities = {Note.class}, version = 2, exportSchema = false)
@TypeConverters(value = {Database.class})
public abstract class Database extends RoomDatabase {

    public abstract NoteDao getNoteDao();

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Note ADD COLUMN deletedLocally INTEGER NOT NULL DEFAULT 0");
        }
    };

    public static Database getInstance(Context context) {
        return Room.databaseBuilder(context, Database.class, "DB_NOTES")
                .addMigrations(MIGRATION_1_2)
                .build();
    }

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

}
