package com.example.notesapp.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.example.notesapp.db.Database;
import com.example.notesapp.db.Note;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String SERVER = "http://10.0.2.2/notesapp/";

    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

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
            syncFromServerToClient();
            System.out.println("SYNCING FINISHED");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void syncFromServerToClient() throws Exception {
        Date lastKnownExternalChanged = database.getNoteDao().getMaximalExternalChanged();
        String changedFilterParam = lastKnownExternalChanged != null ? "?changedFrom=" + URLEncoder.encode(formatter.format(lastKnownExternalChanged), "UTF-8") : "";

        RequestQueue queue = Volley.newRequestQueue(getContext());
        RequestFuture<JSONArray> future = RequestFuture.newFuture();
        JsonRequest request = new JsonArrayRequest(SERVER + "notes.php" + changedFilterParam, future, future);
        queue.add(request);
        JSONArray response = future.get();
        System.out.println("OBTAINED RESPONSE");

        for (int i = 0; i < response.length(); i++) {
            JSONObject json = response.getJSONObject(i);
            int externalId = json.getInt("id");
            Date externalChanged = formatter.parse(json.getString("changed"));
            String name = json.getString("name");
            String text = json.getString("text");

            Note note = database.getNoteDao().getByExternalId(externalId);
            if (note == null) {
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
