package com.example.notesapp.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NoteDao {

    @Insert
    long insert(Note note);

    @Update
    void update(Note note);

    @Delete
    void delete(Note note);

    @Query("SELECT * FROM Note WHERE id = :id AND deleted = 0")
    Note getById(long id);

    @Query("SELECT * FROM Note WHERE deleted = 0 ORDER BY changed DESC")
    List<Note> getAll();

    @Query("SELECT * FROM Note WHERE id = :id AND deleted = 0")
    LiveData<Note> getByIdAsync(long id);

    @Query("SELECT * FROM Note WHERE deleted = 0 ORDER BY changed DESC")
    LiveData<List<Note>> getAllAsync();

}
