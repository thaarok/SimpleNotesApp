package com.example.notesapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import com.example.notesapp.db.Note;
import com.example.notesapp.db.NoteRepository;
import com.google.android.material.snackbar.Snackbar;

public class EditActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE_ID = "com.example.notesapp.noteId";

    private NoteRepository repository;
    private Note openedNote;
    private EditText editName;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Intent intent = getIntent();
        int id = intent.getIntExtra(EXTRA_MESSAGE_ID, 0);
        if (id == 0) throw new IllegalArgumentException();

        repository = new NoteRepository(getApplication());

        editName = findViewById(R.id.editName);
        editText = findViewById(R.id.editText);

        repository.getById(id).observe(this, new Observer<Note>() {
            @Override
            public void onChanged(Note note) {
                openedNote = note;
                editName.setText(openedNote.getName());
                editText.setText(openedNote.getText());
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        openedNote.setName(editName.getText().toString());
        openedNote.setText(editText.getText().toString());
        repository.update(openedNote);
    }

}
