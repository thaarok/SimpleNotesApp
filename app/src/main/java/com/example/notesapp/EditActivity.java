package com.example.notesapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.example.notesapp.db.Database;
import com.example.notesapp.db.Note;

public class EditActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE_ID = "com.example.notesapp.noteId";

    private Database database;
    private Note openedNote;
    private EditText editName;
    private EditText editText;
    private ImageButton removeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        database = Database.getInstance(getApplicationContext());

        Intent intent = getIntent();
        long id = intent.getLongExtra(EXTRA_MESSAGE_ID, 0);
        if (id == 0) throw new IllegalArgumentException();

        editName = findViewById(R.id.editName);
        editText = findViewById(R.id.editText);
        removeButton = findViewById(R.id.removeButton);

        database.getNoteDao().getByIdAsync(id).observe(this, new Observer<Note>() {
            @Override
            public void onChanged(Note note) {
                openedNote = note;
                if (openedNote == null) return;
                editName.setText(openedNote.getName());
                editText.setText(openedNote.getText());
            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openedNote.setDeletedLocally(true);
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        openedNote.setName(editName.getText().toString());
        openedNote.setText(editText.getText().toString());
        openedNote.setChangedLocally(true);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                database.getNoteDao().update(openedNote);
                System.out.println("UPDATED");
                return null;
            }
        }.execute();
    }

}
