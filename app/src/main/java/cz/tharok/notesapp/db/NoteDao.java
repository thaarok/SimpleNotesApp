package cz.tharok.notesapp.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.Date;
import java.util.List;

@Dao
public abstract class NoteDao {

    @Insert
    public abstract long insert(Note note);

    @Update
    public abstract void update(Note note);

    @Delete
    public abstract void delete(Note note);

    @Query("SELECT MAX(externalChanged) FROM Note")
    public abstract Date getMaximalExternalChanged();

    @Query("SELECT * FROM Note WHERE externalId = :externalId")
    public abstract Note getByExternalId(long externalId);

    @Query("SELECT * FROM Note WHERE changedLocally != 0")
    public abstract List<Note> getChangedLocally();

    @Query("SELECT * FROM Note WHERE deletedLocally != 0")
    public abstract List<Note> getDeletedLocally();

    @Query("SELECT * FROM Note WHERE id = :id AND deletedLocally = 0")
    public abstract LiveData<Note> getByIdAsync(long id);

    @Query("SELECT * FROM Note WHERE deletedLocally = 0 ORDER BY externalChanged DESC")
    public abstract LiveData<List<Note>> getNotesAsync();

}
