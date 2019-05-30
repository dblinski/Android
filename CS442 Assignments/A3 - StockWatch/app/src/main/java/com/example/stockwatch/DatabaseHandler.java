package com.example.stockwatch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHandler";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "StocksDB";
    private static final String TABLE_NAME = "StockTable";

    private static final String SYMBOL = "Symbol";
    private static final String COMPANYNAME = "CompanyName";

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    SYMBOL + " TEXT not null unique, " +
                    COMPANYNAME + " TEXT not null)";

    private SQLiteDatabase database;

    public DatabaseHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        Log.d(TAG, "onCreate: Making new DB");
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
    }

    public ArrayList<String[]> loadStocks(){
        Log.d(TAG, "loadStocks: loadStocks: Start");
        ArrayList<String[]> stockList = new ArrayList<>();

        Cursor cursor = database.query(
                TABLE_NAME,
                new String[]{SYMBOL, COMPANYNAME},
                null,
                null,
                null,
                null,
                null);
        if(cursor != null){
            cursor.moveToFirst();
            for(int i = 0; i < cursor.getCount(); i++){
                String symbol = cursor.getString(0);
                String companyName = cursor.getString(1);
                stockList.add(new String[]{symbol, companyName});
                cursor.moveToNext();
            }
            cursor.close();
        }
        Log.d(TAG, "loadStocks: DONE");
        return stockList;
    }

    public void addStock(Stock stock){
        ContentValues values = new ContentValues();

        values.put(SYMBOL, stock.getSymbol());
        values.put(COMPANYNAME, stock.getCompanyName());

        long key = database.insert(TABLE_NAME, null, values);
        Log.d(TAG, "addStock: " + key);
    }
    public void deleteStock(String symbol){
        Log.d(TAG, "deleteStock: " + symbol);

        int c = database.delete(TABLE_NAME, SYMBOL + " = ?",  new String[]{symbol});

        Log.d(TAG, "deleteStock: " + c);
    }

    public void shutDown(){
        database.close();
    }
}
