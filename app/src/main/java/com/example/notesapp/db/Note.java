package com.example.notesapp.db;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;

@Entity
public class Note {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private long externalId;

    private Date externalChanged; // from last sync, not update locally

    private String name;

    private String text;

    private boolean changedLocally;

    private boolean deletedLocally;

    public Note(long externalId, Date externalChanged, String name, String text) {
        this.externalId = externalId;
        this.externalChanged = externalChanged;
        this.name = name;
        this.text = text;
    }

    @Ignore
    public Note(long externalId) {
        this.externalId = externalId;
    }

    @Ignore
    public Note(String name, String text) {
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

    public boolean getChangedLocally() {
        return changedLocally;
    }

    public void setChangedLocally(boolean changedLocally) {
        this.changedLocally = changedLocally;
    }

    public boolean isDeletedLocally() {
        return deletedLocally;
    }

    public void setDeletedLocally(boolean deletedLocally) {
        this.deletedLocally = deletedLocally;
    }

    public void parseServerJson(JSONObject json) throws JSONException, ParseException {
        name = json.getString("name");
        text = json.getString("text");
        externalChanged = Database.formatter.parse(json.getString("changed"));
    }

    public JSONObject toServerJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("id", externalId);
        json.put("name", name);
        json.put("text", text);
        return json;
    }

}
