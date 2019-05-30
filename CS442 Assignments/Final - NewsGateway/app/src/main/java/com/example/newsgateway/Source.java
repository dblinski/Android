package com.example.newsgateway;

import java.io.Serializable;

public class Source implements Serializable {
    String id;
    String name;
    String category;

    public Source(String id, String name, String category){
        this.id = id;
        this.name = name;
        this.category = category;
    }
    public String getId() {
        return id;
    }
    public String getName(){
        return name;
    }
    public String getCategory() {
        return category;
    }
}
