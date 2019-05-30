package com.example.stockwatch;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class StockAdapter extends RecyclerView.Adapter<StockViewHolder> {
    private List<Stock> stockList;
    private MainActivity mainActivity;

    public StockAdapter(List<Stock> stockList, MainActivity ma){
        this.stockList = stockList;
        mainActivity = ma;
    }
    @NonNull
    @Override
    public StockViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i){
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.stock_entry, viewGroup, false);
        itemView.setOnClickListener(mainActivity);
        itemView.setOnLongClickListener(mainActivity);

        return new StockViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(@NonNull StockViewHolder stockViewHolder, int i){
        NumberFormat usd = NumberFormat.getInstance(Locale.US);
        usd.setMaximumFractionDigits(2);
        usd.setMinimumIntegerDigits(1);

        Stock stock = stockList.get(i);

        stockViewHolder.symbolTextView.setText(stock.getSymbol());
        stockViewHolder.nameTextView.setText(stock.getCompanyName());
        stockViewHolder.priceTextView.setText(usd.format(stock.getPrice()));
        String pc = usd.format(stock.getPriceChange()) + " (" + usd.format(stock.getChangePercentage()) + ")";

        int color;
        if(stock.getPriceChange() >= 0){
            color = 0xFF00FF00;
            pc = "▲ " + pc;
        }
        else{
            color = 0xFFFF0000;
            pc = "▼ " + pc;
        }
        stockViewHolder.priceChangeTextView.setText(pc);

        stockViewHolder.symbolTextView.setTextColor(color);
        stockViewHolder.nameTextView.setTextColor(color);
        stockViewHolder.priceTextView.setTextColor(color);
        stockViewHolder.priceChangeTextView.setTextColor(color);
    }
    @Override
    public int getItemCount(){
        return stockList.size();
    }
}
