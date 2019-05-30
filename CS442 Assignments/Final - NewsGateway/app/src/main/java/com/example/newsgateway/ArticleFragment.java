package com.example.newsgateway;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;
import com.squareup.picasso.Picasso;

public class ArticleFragment extends Fragment {
    private static final String TAG = "ArticleFragment";
    public ArticleFragment(){
        setRetainInstance(true);
    }

    public static ArticleFragment newInstance(Article article, int index, int total){
        Log.d(TAG, "newInstance: newInstance fragment");
        ArticleFragment articleFragment = new ArticleFragment();
        Bundle bundle = new Bundle(1);
        bundle.putSerializable("ARTICLE_DATA", article);
        bundle.putSerializable("INDEX", index);
        bundle.putSerializable("TOTAL", total);
        articleFragment.setArguments(bundle);
        return articleFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        Log.d(TAG, "onCreateView: fragment onCreateView");
        View fragmentLayout = inflater.inflate(R.layout.fragment, container, false);
        final Article currentArticle = (Article) getArguments().getSerializable("ARTICLE_DATA");
        int index = getArguments().getInt("INDEX");
        int total = getArguments().getInt("TOTAL");

        TextView title = fragmentLayout.findViewById(R.id.fragmentTitle);
        title.setText(currentArticle.getTitle());

        TextView author = fragmentLayout.findViewById(R.id.fragmentAuthor);
        author.setText(currentArticle.getAuthor());

        TextView date = fragmentLayout.findViewById(R.id.fragmentDate);
        date.setText(currentArticle.getPublishedAt());

        TextView desc = fragmentLayout.findViewById(R.id.fragmentArticle);
        desc.setText(currentArticle.getDescription());

        TextView counter = fragmentLayout.findViewById(R.id.fragmentCounter);
        counter.setText(String.format(Locale.US, "%d of %d", index,  total));

        ImageView image = fragmentLayout.findViewById(R.id.fragmentImage);
        final String imageURL = currentArticle.getUrlToImage();
        Context context = getActivity().getApplicationContext();
        Picasso picasso = new Picasso.Builder(context).build();
        picasso.load(imageURL).error(R.drawable.no_image).placeholder(R.drawable.no_image).into(image);

        final String articleURL = currentArticle.getUrl();
        title.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                onSomethingClicked(articleURL);
            }
        });
        author.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                onSomethingClicked(articleURL);
            }
        });
        date.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                onSomethingClicked(articleURL);
            }
        });
        desc.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                onSomethingClicked(articleURL);
            }
        });
        image.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                onSomethingClicked(articleURL);
            }
        });
        Log.d(TAG, "onCreateView: fragment after onCreateView");
        return fragmentLayout;
    }

    public void onSomethingClicked(String url){
        Uri imageUri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(imageUri);
        startActivity(intent);
    }
}
