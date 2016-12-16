package com.example.tkft1.bfe2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class tab02_court_detail extends Activity implements  View.OnClickListener{
    private int position = -1;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab02_vicinity_detail);

        Intent intent = getIntent();
        position = intent.getIntExtra("com.example.tkft1.bfe2.information",-1);
        CourtList Court = new CourtList();
        TextView textView = (TextView)findViewById(R.id.court_name);
        textView.setText(Court.getlist().get(position).get("court_name").toString());
        textView = (TextView)findViewById(R.id.court_distance);
        textView.setText(Court.getlist().get(position).get("court_distance").toString());
        textView = (TextView)findViewById(R.id.court_position);
        textView.setText(Court.getlist().get(position).get("court_position").toString());

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.gothere:
                    Intent intent = new Intent();

                    ArrayList<String> informaion = new ArrayList<>();
                    CourtList court = new CourtList();
                    informaion.add(court.getlist().get(position).get("court_city").toString());
                    informaion.add(court.getlist().get(position).get("court_name").toString());
                    intent.putExtra("com.example.tkft1.bfe2.information",informaion);
                    intent.setClass(tab02_court_detail.this, RoutePlanningActivity.class);
                    startActivity(intent);
            }
        }
}

