package com.example.tkft1.bfe2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

public class tab03_my_informaion extends Activity{

    private TextView textView;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab03_my_information);

        Intent intent = getIntent();
        ArrayList<String> information = intent.getStringArrayListExtra("com.example.tkft1.bfe2.information");
        textView = (TextView)findViewById(R.id.username);
        textView.setText(information.get(0));
        textView = (TextView)findViewById(R.id.password);
        textView.setText(information.get(1));
    }
}

