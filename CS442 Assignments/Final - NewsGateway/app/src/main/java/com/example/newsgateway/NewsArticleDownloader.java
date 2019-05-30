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

public class NewsArticleDownloader extends AsyncTask<String, Void, String> {
    private NewsService newsService;
    private Source source;

    private final String API_KEY = "a6667122589f40f99865cd01759517e4";
    private static final String TAG = "NewsArticleDownloader";
    public NewsArticleDownloader(NewsService newsService, Source source){
        this.newsService = newsService;
        this.source = source;
    }
    @Override
    protected String doInBackground(String... parms){
        String newsURL = "https://newsapi.org/v2/everything?sources=" + source.getId() + "&language=en&pageSize=100&apiKey=" + API_KEY;
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
        } catch(Exception e){
            return null;
        }
        return stringBuilder.toString();
    }
    @Override
    protected void onPostExecute(String articles){
        ArrayList<Article> list = parseJSON(articles);
        newsService.setArticles(list);
    }
    private ArrayList<Article> parseJSON(String str){
        ArrayList<Article> tempList = new ArrayList<>();
        try{
            JSONObject jsonObject = new JSONObject(str);
            JSONArray jsonArray = jsonObject.getJSONArray("articles");
            int stop;
            if(jsonArray.length() > 10){
                stop = 10;
            }
            else{
                stop = jsonArray.length();
            }
            for(int i = 0; i < stop; i++){
                JSONObject jsonArticle = (JSONObject) jsonArray.get(i);
                String author = jsonArticle.getString("author");
                String title = jsonArticle.getString("title");
                String description = jsonArticle.getString("description");
                String url = jsonArticle.getString("url");
                String urlToImage = jsonArticle.getString("urlToImage");
                String publishedAt = jsonArticle.getString("publishedAt");
                if(url.isEmpty() || url.equals("null"))
                    continue;
                if(author.isEmpty() || author.equals("null"))
                    author = "Author not specified.";
                if(title.isEmpty() || title.equals("null"))
                    title = "Title not specified.";
                if(description.isEmpty() || description.equals("null"))
                    description = "No description.";
                Log.d(TAG, "parseJSON: t");
                tempList.add(new Article(author, title, description, url, urlToImage, publishedAt));
            }
            return tempList;
        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
