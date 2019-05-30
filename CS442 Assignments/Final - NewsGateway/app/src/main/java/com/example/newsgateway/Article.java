package com.example.newsgateway;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Article implements Serializable {
    String author;
    String title;
    String description;
    String url;
    String urlToImage;
    String publishedAt;

    public Article(String author, String title, String description, String url, String urlToImage, String publishedAt) throws Exception{
        this.author = author;
        this.title = title;
        this.description = description;
        this.url = url;
        this.urlToImage = urlToImage;
        setPublishedAt(publishedAt);
    }

    public String getAuthor(){
        return author;
    }
    public String getTitle(){
        return title;
    }
    public String getDescription() {
        return description;
    }
    public String getUrl() {
        return url;
    }
    public String getUrlToImage() {
        return urlToImage;
    }
    public String getPublishedAt() {
        return publishedAt;
    }
    public void setPublishedAt(String date) throws Exception {
        //e.g. 2019-04-15T14:42:45Z
        //("MMM dd, yyyy HH:mm");
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");
        Date date1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(date);
        publishedAt = sdf.format(date1);
    }
}
