package com.example.stockwatch;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class NameDownloader extends AsyncTask<String, Void, String> {
    private static final String TAG = "NameDownloader";
    private MainActivity mainActivity;

    private final String SYMBOLS_URL = "https://api.iextrading.com/1.0/ref-data/symbols";

    public NameDownloader(MainActivity ma){
        mainActivity = ma;
    }

    @Override
    protected String doInBackground(String... params){
        Uri dataUri = Uri.parse(SYMBOLS_URL);
        String urlToUse = dataUri.toString();
        Log.d(TAG, "doInBackground: URL: " + urlToUse);

        StringBuilder sb = new StringBuilder();
        try{
            URL url = new URL(urlToUse);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            Log.d(TAG, "doInBackground: response code: " + conn.getResponseCode());
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while((line = reader.readLine()) != null){
                sb.append(line).append('\n');
            }
            Log.d(TAG, "doInBackground: String: " + sb.toString());
        } catch(Exception e){
            Log.e(TAG, "doInBackground: " + sb.toString());
            return null;
        }

        return sb.toString();
    }
    @Override
    protected void onPostExecute(String str){
        mainActivity.updateNames(parseJSON(str));
    }

    private HashMap<String, String> parseJSON(String str){
        HashMap<String, String> stocksMap = new HashMap<>();
        try{
            JSONArray jsonArray = new JSONArray(str); //array of all stock symbols&names
            for(int i = 0;  i < jsonArray.length(); i++){
                JSONObject jStock = (JSONObject) jsonArray.get(i); //object w/ stock symbol&name
                String symbol = jStock.getString("symbol");
                String name = jStock.getString("name");
                stocksMap.put(symbol, name);
            }
            return stocksMap;
        } catch(Exception e){
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
