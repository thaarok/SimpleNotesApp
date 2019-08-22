package com.example.notesapp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notesapp.db.Note;
import com.example.notesapp.db.NoteViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NoteViewModel repository;
    private NoteAdapter noteAdapter;
    private Account account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Note note = new Note(0, "new", "new text");
                repository.insertAsync(note);
                refreshList();

                /*
                // has to wait for insert finish
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra(EXTRA_MESSAGE_ID, note.getId());
                startActivity(intent);
                */
            }
        });

        repository = ViewModelProviders.of(this).get(NoteViewModel.class);
        noteAdapter = new NoteAdapter(this);

        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(noteAdapter);

        account = createSyncAccount();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    private void refreshList() {
        repository.getAll().observe(this, noteAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu); // items to the action bar
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { // action bar (three dots)
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_sync) {
            Bundle settingsBundle = new Bundle();
            settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
            settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
            ContentResolver.requestSync(account, "com.example.notesapp.provider", settingsBundle);
            System.out.println("REQUESTED SYNC");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Account createSyncAccount() {
        String ACCOUNT_TYPE = "com.example.notesapp.account";
        AccountManager accountManager = (AccountManager) getSystemService(ACCOUNT_SERVICE);

        Account[] existing = accountManager.getAccountsByType(ACCOUNT_TYPE);
        if (existing.length > 0) {
            return existing[0];
        }

        Account account = new Account("dummyaccount", ACCOUNT_TYPE);
        if (accountManager.addAccountExplicitly(account, null, null)) {
            ContentResolver.setIsSyncable(account, "com.example.notesapp.content", 1);
            ContentResolver.setSyncAutomatically(account, "com.example.notesapp.content", true);
        }
        return account;
    }

}
