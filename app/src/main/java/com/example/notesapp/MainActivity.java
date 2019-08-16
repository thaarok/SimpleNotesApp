package com.example.notesapp;

import android.content.Intent;
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
import com.example.notesapp.db.NoteRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import static com.example.notesapp.EditActivity.EXTRA_MESSAGE_ID;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NoteRepository repository;
    private NoteAdapter noteAdapter;

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
                repository.insert(note);
                refreshList();

                /*
                // has to wait for insert finish
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra(EXTRA_MESSAGE_ID, note.getId());
                startActivity(intent);
                */
            }
        });

        repository = ViewModelProviders.of(this).get(NoteRepository.class);
        noteAdapter = new NoteAdapter(this);

        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(noteAdapter);
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

        return super.onOptionsItemSelected(item);
    }
}
