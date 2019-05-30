package com.example.stockwatch;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class StockViewHolder extends RecyclerView.ViewHolder {
    public TextView symbolTextView, nameTextView, priceTextView, priceChangeTextView;
    public StockViewHolder(@NonNull View view){
        super(view);
        symbolTextView = view.findViewById(R.id.symbolEntry);
        nameTextView = view.findViewById(R.id.nameEntry);
        priceTextView = view.findViewById(R.id.priceEntry);
        priceChangeTextView = view.findViewById(R.id.priceChangeEntry);
    }
}
