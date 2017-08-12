package com.example.tkft1.bfe2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class tab01_new_detail extends Activity{
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab01_news_detail);

        Intent intent = getIntent();
        int position = intent.getIntExtra("com.example.tkft1.bfe2.information",-1);
        NewsList news = new NewsList();
        TextView textView = (TextView)findViewById(R.id.user);
        textView.setText(news.getlist().get(position).get("user").toString());
        textView = (TextView)findViewById(R.id.position);
        textView.setText(news.getlist().get(position).get("position").toString());
        textView = (TextView)findViewById(R.id.introduction);
        textView.setText(news.getlist().get(position).get("introduction").toString());
    }
}

