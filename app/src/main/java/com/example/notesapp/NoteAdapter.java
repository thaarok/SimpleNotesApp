package com.example.notesapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notesapp.db.Note;

import java.util.Collections;
import java.util.List;

import static com.example.notesapp.EditActivity.EXTRA_MESSAGE_ID;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder> implements Observer<List<Note>> {

    private List<Note> notes = Collections.emptyList();
    private Context context;

    public NoteAdapter(Context context) {
        this.context = context;
    }

    @Override
    public void onChanged(List<Note> notes) {
        System.out.println("ON CHANGE");
        this.notes = notes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_row, parent, false);
        return new NoteHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
        holder.bind(notes.get(position));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    class NoteHolder extends RecyclerView.ViewHolder {

        private TextView txtName;

        public NoteHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
        }

        public void bind(final Note note) {
            txtName.setText(note.getName());
            txtName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, EditActivity.class);
                    intent.putExtra(EXTRA_MESSAGE_ID, note.getId());
                    context.startActivity(intent);
                }
            });
        }

    }
}
