package com.example.notesapp.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.notesapp.db.Database;
import com.example.notesapp.db.Note;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Date;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String SERVER = "http://10.0.2.2/notesapp/";

    private Database database;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        database = Database.getInstance(context);
    }

    // for compatibility with Android 3.0 and later
    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        database = Database.getInstance(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        System.out.println("SYNCING...");
        try {
            syncFromClientToServer();
            syncFromServerToClient();
            System.out.println("SYNCING FINISHED");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void syncFromClientToServer() throws Exception {
        RequestQueue queue = Volley.newRequestQueue(getContext());

        for (Note note : database.getNoteDao().getChangedLocally()) {

            RequestFuture<JSONObject> future = RequestFuture.newFuture();
            JsonRequest request = new JsonObjectRequest(Request.Method.PUT, SERVER + "notes.php", note.toJson(), future, future);
            queue.add(request);
            JSONObject response = future.get();

            note.setExternalId(response.getLong("id"));
            database.getNoteDao().update(note);
            System.out.println("SYNC REMOTE UPDATE");

        }

        for (Note note : database.getNoteDao().getDeletedLocally()) {

            RequestFuture<String> future = RequestFuture.newFuture();
            StringRequest request = new StringRequest(Request.Method.DELETE, SERVER + "notes.php?id=" + note.getExternalId(), future, future);
            queue.add(request);
            future.get();

            database.getNoteDao().delete(note);
            System.out.println("SYNC REMOTE DELETE");

        }
    }

    private void syncFromServerToClient() throws Exception {
        Date lastKnownExternalChanged = database.getNoteDao().getMaximalExternalChanged();
        String changedFilterParam = lastKnownExternalChanged != null ? "?changedFrom=" + URLEncoder.encode(Database.formatter.format(lastKnownExternalChanged), "UTF-8") : "";
        RequestQueue queue = Volley.newRequestQueue(getContext());

        RequestFuture<JSONArray> future = RequestFuture.newFuture();
        JsonRequest request = new JsonArrayRequest(SERVER + "notes.php" + changedFilterParam, future, future);
        queue.add(request);
        JSONArray response = future.get();
        System.out.println("OBTAINED RESPONSE");

        for (int i = 0; i < response.length(); i++) {
            JSONObject json = response.getJSONObject(i);
            int externalId = json.getInt("id");
            Date externalChanged = Database.formatter.parse(json.getString("changed"));
            boolean deleted = json.getBoolean("deleted");
            String name = json.getString("name");
            String text = json.getString("text");

            Note note = database.getNoteDao().getByExternalId(externalId);
            if (deleted && note != null) {
                database.getNoteDao().delete(note);
                System.out.println("SYNC LOCAL DELETE");
            } else if (note == null) {
                note = new Note(externalId, externalChanged, name, text);
                database.getNoteDao().insert(note);
                System.out.println("SYNC LOCAL INSERT");
            } else {
                note.setName(name);
                note.setText(text);
                database.getNoteDao().update(note);
                System.out.println("SYNC LOCAL UPDATE");
            }
        }
    }

}
