package com.example.notesapp.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.example.notesapp.db.Database;
import com.example.notesapp.db.Note;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

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
            RequestQueue queue = Volley.newRequestQueue(getContext());
            RequestFuture<JSONArray> future = RequestFuture.newFuture();
            JsonRequest request = new JsonArrayRequest("http://10.0.2.2/notesapp/notes.php", future, future);
            queue.add(request);
            JSONArray response = future.get();
            System.out.println("OBTAINED RESPONSE");

            for (int i = 0; i < response.length(); i++) {
                JSONObject json = response.getJSONObject(i);
                int id = json.getInt("id");
                String name = json.getString("name");
                String text = json.getString("text");

                Note note = database.getNoteDao().getById(id);
                if (note == null) {
                    note = new Note(id, name, text);
                    database.getNoteDao().insert(note);
                } else {
                    note.setName(name);
                    note.setText(text);
                    database.getNoteDao().update(note);
                }
            }
            System.out.println("HTTP RESPONSE PROCESSED");


        } catch (InterruptedException | ExecutionException | JSONException e) {
            throw new RuntimeException(e);
        }

    }

}
