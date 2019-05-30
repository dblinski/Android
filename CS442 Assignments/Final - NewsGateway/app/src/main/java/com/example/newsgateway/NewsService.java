package com.example.newsgateway;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;

public class NewsService extends Service {
    private ArrayList<Article> articlesList = new ArrayList<>();
    private boolean isRunning = true;
    ServiceReceiver serviceReceiver;
    private static final String TAG = "NewsService";
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.d(TAG, "onStartCommand: NewsService started");
        serviceReceiver = new ServiceReceiver();
        IntentFilter filter = new IntentFilter(MainActivity.ACTION_MSG_TO_SVC);
        registerReceiver(serviceReceiver, filter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: service thread running");
                while(isRunning){
                    if(articlesList.isEmpty()) {
                        try {
                            Thread.sleep(250);
                        } catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                    else{
                        Intent serviceIntent = new Intent();
                        serviceIntent.setAction(MainActivity.ACTION_NEWS_STORY);
                        serviceIntent.putExtra(MainActivity.ARTICLES_DATA, articlesList);
                        sendBroadcast(serviceIntent);
                        articlesList.clear();
                    }
                }
            }
        }).start();
        return Service.START_STICKY;
    }
    @Override
    public void onDestroy(){
        unregisterReceiver(serviceReceiver);
        isRunning = false;
        super.onDestroy();
    }

    public void setArticles(ArrayList<Article> list){
        articlesList.clear();
        articlesList.addAll(list);
        Log.d(TAG, "setArticles: t");
    }

    class ServiceReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent){
            if(intent.getAction().equals(MainActivity.ACTION_MSG_TO_SVC)){
                Source source = (Source) intent.getSerializableExtra(MainActivity.SOURCE_DATA);
                new NewsArticleDownloader(NewsService.this, source).execute();
                Log.d(TAG, "onReceive: t");
            }
        }
    }
}
