package com.example.stockwatch;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class Stock implements Serializable, Comparable<Stock> {
    private String symbol, companyName;
    private double price, change, changePercent;

    public Stock(String sym, String n, double p, double ch, double ch_percent){
        symbol = sym;
        companyName = n;
        price = p;
        change = ch;
        changePercent = ch_percent;
    }
    public String getSymbol(){
        return symbol;
    }
    public String getCompanyName(){
        return companyName;
    }
    public double getPrice(){
        return price;
    }
    public double getPriceChange(){
        return change;
    }
    public double getChangePercentage(){
        return changePercent;
    }

    @Override
    public int compareTo(@NonNull Stock o){
        return getSymbol().compareTo(o.getSymbol());
    }
}
