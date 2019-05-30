package com.example.newsgateway;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Source> sourceList = new ArrayList<>();
    private ArrayList<SpannableString> sourceNames = new ArrayList<>();
    private HashMap<String, Integer> categoryMap;

    private NewsReceiver newsReceiver;
    private Menu mainMenu;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private List<Fragment> fragments;
    private MyPageAdapter pageAdapter;
    private ViewPager pager;

    private Source currentSource;

    static final String ACTION_NEWS_STORY = "ACTION_NEWS_STORY";
    static final String ACTION_MSG_TO_SVC = "ACTION_MSG_TO_SVC";
    static final String SOURCE_DATA = "SOURCE_DATA";
    static final String ARTICLES_DATA = "ARTICLES_DATA";

    static final int []COLORS = {Color.BLACK, Color.CYAN, Color.GREEN, Color.MAGENTA, 0xFFDB8D87, 0xFFBA9F18,
                                    0xFFAD0516, 0xFF7DAD05, 0xFF05AD7D, 0xFF6BA1DB, 0xFFDBC86B, Color.RED, Color.LTGRAY, 0xFFD89F0D};
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(MainActivity.this, NewsService.class));
        newsReceiver = new NewsReceiver();
        mDrawerLayout = findViewById(R.id.drawerLayout);
        mDrawerList = findViewById(R.id.drawerList);
        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_item, sourceNames));
        mDrawerList.setOnItemClickListener(
                new ListView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                        selectItem(position);
                    }
        });
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close);
        fragments = new ArrayList<>();
        pageAdapter = new MyPageAdapter(getSupportFragmentManager());
        pager = findViewById(R.id.viewPager);
        pager.setAdapter(pageAdapter);
        if(sourceList.isEmpty()){
            new NewsSourceDownloader(this, "all").execute();
            Log.d(TAG, "onCreate: asynctask");
        }
    }
    @Override
    protected void onResume(){
        IntentFilter filter = new IntentFilter(ACTION_NEWS_STORY);
        registerReceiver(newsReceiver, filter);
        super.onResume();
    }
    @Override
    protected void onStop(){
        unregisterReceiver(newsReceiver);
        super.onStop();
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    public boolean onOptionsItemSelected(MenuItem item){
        if(mDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        new NewsSourceDownloader(this, item.getTitle().toString()).execute();
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu, menu);
        mainMenu = menu;
        return true;
    }
    private void selectItem(int position){
        pager.setBackground(null);
        currentSource = sourceList.get(position);
        Intent intent = new Intent();
        intent.setAction(ACTION_MSG_TO_SVC);
        intent.putExtra(SOURCE_DATA, currentSource);
        sendBroadcast(intent);
        mDrawerLayout.closeDrawer(mDrawerList);
    }
    private void reDoFragments(ArrayList<Article> articles){
        Log.d(TAG, "reDoFragments: t");
        setTitle(currentSource.getName());
        for(int i = 0; i < pageAdapter.getCount(); i++){
            pageAdapter.notifyChangeInPosition(i);
        }
        fragments.clear();
        for(int i = 0; i < articles.size(); i++){
            fragments.add(ArticleFragment.newInstance(articles.get(i),i+1, articles.size()));
        }
        pageAdapter.notifyDataSetChanged();
        pager.setCurrentItem(0);
    }
    public void setSources(ArrayList<Source> sources, ArrayList<String> categories){
        if(categoryMap == null){
            categoryMap = new HashMap<>();
            int count = 0;
            for(String s : categories){
                categoryMap.put(s, COLORS[count]);
                SpannableString str = new SpannableString(s);
                str.setSpan(new ForegroundColorSpan(COLORS[count]),0,str.length(),0);
                mainMenu.add(str);
                count++;
            }
        }
        sourceList.clear();
        sourceList.addAll(sources);
        sourceNames.clear();
        for(Source s : sources){
            SpannableString str = new SpannableString(s.getName());
            str.setSpan(new ForegroundColorSpan(categoryMap.get(s.getCategory())),0,str.length(),0);
            sourceNames.add(str);
        }
        ((ArrayAdapter) mDrawerList.getAdapter()).notifyDataSetChanged();
    }
    private class NewsReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent){
            if(intent.getAction().equals(ACTION_NEWS_STORY)){
                ArrayList<Article> tempArticleList = (ArrayList<Article>) intent.getSerializableExtra(ARTICLES_DATA);
                reDoFragments(tempArticleList);
            }
        }
    }
    private class MyPageAdapter extends FragmentPagerAdapter{
        private long baseId = 0;
        MyPageAdapter(FragmentManager fragmentManager){
            super(fragmentManager);
        }
        @Override
        public int getItemPosition(@NonNull Object object){
            return POSITION_NONE;
        }
        @Override
        public Fragment getItem(int position){
            return fragments.get(position);
        }
        @Override
        public int getCount(){
            return fragments.size();
        }
        @Override
        public long getItemId(int position){
            return baseId + position;
        }
        void notifyChangeInPosition(int n){
            baseId += getCount() + n;
        }
    }
}