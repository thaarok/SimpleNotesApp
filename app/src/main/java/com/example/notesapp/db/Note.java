package com.example.notesapp.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class Note {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private long externalId;

    private Date externalChanged; // from last sync, not update locally

    private String name;

    private String text;

    private Date changedLocally;

    private boolean deletedLocally;

    public Note(long externalId, Date externalChanged, String name, String text) {
        this.externalId = externalId;
        this.externalChanged = externalChanged;
        this.name = name;
        this.text = text;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getExternalId() {
        return externalId;
    }

    public void setExternalId(long externalId) {
        this.externalId = externalId;
    }

    public Date getExternalChanged() {
        return externalChanged;
    }

    public void setExternalChanged(Date externalChanged) {
        this.externalChanged = externalChanged;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getChangedLocally() {
        return changedLocally;
    }

    public void setChangedLocally(Date changedLocally) {
        this.changedLocally = changedLocally;
    }

    public boolean isDeletedLocally() {
        return deletedLocally;
    }

    public void setDeletedLocally(boolean deletedLocally) {
        this.deletedLocally = deletedLocally;
    }

}
