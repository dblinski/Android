package com.example.stockwatch;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class StockDownloader extends AsyncTask<String, Void, String> {
    private MainActivity mainActivity;
    private String stockURL;
    private static final String TAG = "StockDownloader";
    private boolean addStockToDB;

    public StockDownloader(MainActivity ma, boolean flag){
        mainActivity = ma;
        addStockToDB = flag;
    }

    @Override
    protected String doInBackground(String... params){ //params[0] is the stock symbol
        stockURL = "https://api.iextrading.com/1.0/stock/" + params[0] + "/quote?displayPercent=true";
        Uri dataUri = Uri.parse(stockURL);
        String urlToUse = dataUri.toString();
        Log.d(TAG, "doInBackground: URL: " + urlToUse);

        StringBuilder sb = new StringBuilder();
        try{
            URL url = new URL(urlToUse);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            Log.d(TAG, "doInBackground: " + conn.getResponseCode());
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while((line = reader.readLine()) != null){
                sb.append(line).append('\n');
            }
            Log.d(TAG, "doInBackground: " + sb.toString());
        } catch(Exception e){
            Log.e(TAG, "doInBackground: " + sb.toString());
            return null;
        }
        return sb.toString();
    }
    @Override
    protected void onPostExecute(String str){
        mainActivity.addStock(parseJSON(str), addStockToDB);
    }

    private Stock parseJSON(String str){
        try{
            JSONObject jsonObject = new JSONObject(str); //Stock data object
            String symbol = jsonObject.getString("symbol");
            String name = jsonObject.getString("companyName");
            String priceString = jsonObject.getString("latestPrice");
            double price = Double.parseDouble(priceString);
            String changeString = jsonObject.getString("change");
            double change = Double.parseDouble(changeString);
            String changePercentString = jsonObject.getString("changePercent");
            double changePercent = Double.parseDouble(changePercentString);

            Stock stock = new Stock(symbol,name,price,change,changePercent);
            return stock;
        } catch(Exception e){
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
