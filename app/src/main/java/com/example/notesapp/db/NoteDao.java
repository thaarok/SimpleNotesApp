package com.example.notesapp.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.Date;
import java.util.List;

@Dao
public interface NoteDao {

    @Insert
    long insert(Note note);

    @Update
    void update(Note note);

    @Delete
    void delete(Note note);

    @Query("SELECT MAX(externalChanged) FROM Note")
    Date getMaximalExternalChanged();

    @Query("SELECT * FROM Note WHERE externalId = :externalId")
    Note getByExternalId(long externalId);

    @Query("SELECT * FROM Note WHERE changedLocally != 0")
    List<Note> getChangedLocally();

    @Query("SELECT * FROM Note WHERE deletedLocally != 0")
    List<Note> getDeletedLocally();

    @Query("SELECT * FROM Note WHERE id = :id AND deletedLocally = 0")
    LiveData<Note> getByIdAsync(long id);

    @Query("SELECT * FROM Note WHERE deletedLocally = 0 ORDER BY externalChanged DESC")
    LiveData<List<Note>> getNotesAsync();

}
