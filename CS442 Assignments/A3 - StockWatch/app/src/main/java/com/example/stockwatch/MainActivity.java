package com.example.stockwatch;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private List<Stock> mainStockList = new ArrayList<>();
    private RecyclerView recyclerView;
    private StockAdapter stockAdapter;
    private SwipeRefreshLayout swiper;
    private DatabaseHandler databaseHandler;
    private HashMap<String, String> stockData = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!checkNetwork()){
            openNoNetworkDialog("Stock data cannot be loaded without a network connection.");
            //do more here?
        }
        recyclerView = findViewById(R.id.main_recycler);
        stockAdapter = new StockAdapter(mainStockList, this);
        recyclerView.setAdapter(stockAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        swiper = findViewById(R.id.swiper);
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRefresh();
            }
        });

        databaseHandler = new DatabaseHandler(this);

        doStartingTasks();
    }
    @Override
    protected void onDestroy(){
        databaseHandler.shutDown();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.add_stock){
            if(!checkNetwork()){
                openNoNetworkDialog("Stocks cannot be added without a network connection.");
                return true;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final EditText et = new EditText(this);
            et.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
            et.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
            et.setGravity(Gravity.CENTER_HORIZONTAL);
            builder.setView(et);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    findStock(et.getText().toString());
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });
            builder.setTitle("Stock Selection");
            builder.setMessage("Please enter a Stock Symbol");

            AlertDialog dialog = builder.create();
            dialog.show();
        }
        return true;
    }

    private void findStock(String str){
        if(str.length()>0){
            final ArrayList<String[]> tempList = new ArrayList<>();
            for(String i: stockData.keySet()){
                if(i.matches(str) || i.startsWith(str)){
                    tempList.add(new String[]{i, stockData.get(i)});
                }
                else if(stockData.get(i).startsWith(str)){
                    tempList.add(new String[]{i, stockData.get(i)});
                }
            }
            if(tempList.size() == 0){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Symbol Not Found");
                builder.setMessage("No data found for symbol: " + str);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else if(tempList.size() == 1){
                if(checkDuplicateStock(tempList.get(0)[0])){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Duplicate Stock");
                    builder.setMessage("Stock Symbol " + str + " is already displayed");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else{
                    new StockDownloader(this,true).execute(tempList.get(0)[0]);
                }
            }
            else{
                final CharSequence[] selectionArray = new CharSequence[tempList.size()];
                for(int i = 0; i < tempList.size(); i++){
                    selectionArray[i] = tempList.get(i)[0] + " - " + tempList.get(i)[1];
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Make a selection");
                builder.setItems(selectionArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(checkDuplicateStock(tempList.get(which)[0])){
                            dialog.cancel();
                        }
                        else{
                            new StockDownloader(MainActivity.this,true).execute(tempList.get(which)[0]);
                        }
                    }
                });
                builder.setNegativeButton("Nevermind", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Incorrect input");
            builder.setMessage("No input detected.");
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
    private boolean checkDuplicateStock(String str){
        for(int i = 0; i < mainStockList.size(); i++){
            if(mainStockList.get(i).getSymbol().matches(str)){
                return true;
            }
        }
        return false;
    }

    @Override
    public void onClick(View v){
        int pos = recyclerView.getChildLayoutPosition(v);
        Stock stock = mainStockList.get(pos);
        String symbol = stock.getSymbol();
        String marketWatchURL = "http://www.marketwatch.com/investing/stock/" + symbol;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(marketWatchURL));
        startActivity(i);
    }
    @Override
    public boolean onLongClick(View v){
        int pos = recyclerView.getChildLayoutPosition(v);
        final Stock stock = mainStockList.get(pos);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Stock");
        builder.setMessage("Delete Stock '"+stock.getSymbol()+"'?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                databaseHandler.deleteStock(stock.getSymbol());
                mainStockList.remove(stock);
                stockAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        return false;
    }

    private void doStartingTasks(){
        if(checkNetwork()) {
            new NameDownloader(this).execute();
        }
        ArrayList<String[]> tempList = databaseHandler.loadStocks();
        if(!tempList.isEmpty()){
            if(checkNetwork()){
                for(int i = 0; i < tempList.size(); i++){
                    new StockDownloader(this,false).execute(tempList.get(i)[0]);
                }
            }
            else{
                for(int i = 0; i < tempList.size(); i++){
                    mainStockList.add(new Stock(tempList.get(i)[0],tempList.get(i)[1],0,0,0));
                }
                openNoNetworkDialog("Stocks cannot be loaded without a network connection.");
            }
        }
    }
    private boolean checkNetwork(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            Toast.makeText(this, "Cannot access ConnectivityManager", Toast.LENGTH_SHORT).show();
            return false;
        }

        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()) return true;
        else return false;
    }
    private void openNoNetworkDialog(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Network Connection");
        builder.setMessage(msg);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void doRefresh(){
        if(!checkNetwork()){
            openNoNetworkDialog("Stocks cannot be updated without a network connection.");
            swiper.setRefreshing(false);
            return;
        }
        ArrayList<String []> tempList = databaseHandler.loadStocks();
        mainStockList.clear();
        if(!tempList.isEmpty()){
            for(int i = 0; i < tempList.size(); i++){
                new StockDownloader(this, false).execute(tempList.get(i)[0]);
            }
        }
        swiper.setRefreshing(false);
        Toast.makeText(this,"Stock data refreshed.",Toast.LENGTH_SHORT).show();
    }
    public void addStock(Stock stock, boolean flag){
        if(flag) {
            databaseHandler.addStock(stock);
        }
        mainStockList.add(stock);
        Collections.sort(mainStockList);
        stockAdapter.notifyDataSetChanged();
    }
    public void updateNames(HashMap<String, String> dledMap){
        stockData.clear();
        stockData.putAll(dledMap);
    }
}
