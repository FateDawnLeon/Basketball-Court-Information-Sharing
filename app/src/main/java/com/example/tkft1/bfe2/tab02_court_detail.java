package com.example.tkft1.bfe2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class tab02_court_detail extends Activity{
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab02_vicinity_detail);

        Intent intent = getIntent();
        int position = intent.getIntExtra("com.example.tkft1.bfe2.information",-1);
        CourtList Court = new CourtList();
        TextView textView = (TextView)findViewById(R.id.court_name);
        textView.setText(Court.getlist().get(position).get("court_name").toString());
        textView = (TextView)findViewById(R.id.court_distance);
        textView.setText(Court.getlist().get(position).get("court_distance").toString());
        textView = (TextView)findViewById(R.id.court_position);
        textView.setText(Court.getlist().get(position).get("court_position").toString());
    }
}

