package com.example.notes;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesViewHolder> {
    private static final String TAG = "NotesAdapter";
    private List<Note> notesList;
    private MainActivity mainAct;

    public NotesAdapter(List<Note> notesList, MainActivity ma){
        this.notesList = notesList;
        mainAct = ma;
    }
    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.notes_list_entry, viewGroup, false);
        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);

        return new NotesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder notesViewHolder, int i) {
        Note note = notesList.get(i);
        notesViewHolder.titleTextView.setText(note.getTitleShort());
        notesViewHolder.dateTextView.setText(note.getDate());
        notesViewHolder.contentTextView.setText(note.getContentShort());
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }
}