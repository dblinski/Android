package com.example.newsgateway;

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
import java.util.ArrayList;
import java.util.HashSet;

public class NewsSourceDownloader extends AsyncTask<String, Void, String> {
    private MainActivity mainActivity;
    private String category;

    private ArrayList<Source> sourceList = new ArrayList<>();
    private ArrayList<String> categoryList = new ArrayList<>();

    private final String API_KEY = "a6667122589f40f99865cd01759517e4";

    private static final String TAG = "NewsSourceDownloader";

    public NewsSourceDownloader(MainActivity mainActivity, String category){
        this.mainActivity = mainActivity;
        Log.d(TAG, "NewsSourceDownloader: test?");
        if(category.equalsIgnoreCase("all"))
            this.category = "";
        else
            this.category = category;
    }

    @Override
    protected String doInBackground(String... params){
        String newsURL = "https://newsapi.org/v2/sources?language=en&country=us&category=" + category + "&apiKey=" + API_KEY;
        Log.d(TAG, "doInBackground: url: "+newsURL);
        Uri dataUri = Uri.parse(newsURL);
        String urlToUse = dataUri.toString();
        StringBuilder stringBuilder = new StringBuilder();
        try{
            URL url = new URL(urlToUse);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while((line = reader.readLine()) != null){
                stringBuilder.append(line).append('\n');
            }
            Log.d(TAG, "doInBackground: " + stringBuilder.toString());
        } catch(Exception e){
            return null;
        }
        return stringBuilder.toString();
    }
    @Override
    protected void onPostExecute(String str){
        parseJSON(str);
        mainActivity.setSources(sourceList, categoryList);
    }
    private void parseJSON(String str){
        HashSet<String> categories = new HashSet<>();
        try{
            JSONObject jsonObject = new JSONObject(str);
            JSONArray jsonArray = jsonObject.getJSONArray("sources");

            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonSource = (JSONObject) jsonArray.get(i);
                String id = jsonSource.getString("id");
                String name = jsonSource.getString("name");
                String category = jsonSource.getString("category");

                if(id.isEmpty() || name.isEmpty() || category.isEmpty()){
                    continue;
                }

                categories.add(category);
                sourceList.add(new Source(id, name, category));
            }
            categoryList.add("all");
            categoryList.addAll(categories);
        } catch(Exception e){
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
