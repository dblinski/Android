package com.example.notes;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.io.Serializable;

public class NotesViewHolder extends RecyclerView.ViewHolder
        implements Serializable {
    public TextView titleTextView, dateTextView, contentTextView;
    public NotesViewHolder(@NonNull View view){
        super(view);
        titleTextView = view.findViewById(R.id.titleEntry);
        dateTextView = view.findViewById(R.id.dateEntry);
        contentTextView = view.findViewById(R.id.contentEntry);
    }
}