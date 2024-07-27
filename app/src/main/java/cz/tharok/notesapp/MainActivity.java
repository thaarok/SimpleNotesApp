package cz.tharok.notesapp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notesapp.R;

import cz.tharok.notesapp.db.Database;
import cz.tharok.notesapp.db.Note;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Database database;
    private Account account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        database = Database.getInstance(getApplicationContext());
        final Context context = this;

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditActivity.class);
                context.startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        database.getNoteDao().getNotesAsync().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                System.out.println("NOTES OBSERVED");
                recyclerView.setAdapter(new NoteAdapter(context, notes));
            }
        });

        account = createSyncAccount();
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
            settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true); // ignore disabled sync
            settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true); // to the front of queue
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
