package cz.tharok.notesapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import cz.tharok.notesapp.db.Database;
import cz.tharok.notesapp.db.Note;

public class EditActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE_ID = "cz.tharok.notesapp.noteId";

    private Database database;
    private Note openedNote;
    private EditText editName;
    private EditText editText;
    private ImageButton removeButton;

    private ImageButton saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        database = Database.getInstance(getApplicationContext());
        editName = findViewById(R.id.editName);
        editText = findViewById(R.id.editText);
        removeButton = findViewById(R.id.removeButton);
        saveButton = findViewById(R.id.saveButton);

        Intent intent = getIntent();
        final long id = intent.getLongExtra(EXTRA_MESSAGE_ID, 0);

        if (id == 0) {
            openedNote = new Note("new", "new text");
        } else {
            final LiveData<Note> liveNote = database.getNoteDao().getByIdAsync(id);
            liveNote.observe(this, new Observer<Note>() {
                @Override
                public void onChanged(Note note) {
                    liveNote.removeObserver(this);
                    openedNote = note;
                    if (openedNote == null) throw new IllegalStateException("bad id "+id);
                    editName.setText(openedNote.getName());
                    editText.setText(openedNote.getText());
                }
            });
        }

        removeButton.setOnClickListener(view -> {
            openedNote.setDeletedLocally(true);
            finish();
        });

        saveButton.setOnClickListener(view -> finish());
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
                if (openedNote.getId() == 0) {
                    if ( ! openedNote.isDeletedLocally()) {
                        database.getNoteDao().insert(openedNote);
                    }
                } else {
                    database.getNoteDao().update(openedNote);
                }
                return null;
            }
        }.execute();
    }

}
