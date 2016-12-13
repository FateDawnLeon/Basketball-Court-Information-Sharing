package com.example.tkft1.bfe2;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class tab03_my_friends extends Activity{
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab03_my_friends);

        SimpleAdapter adapter = new SimpleAdapter(this, getNews(), R.layout.tab03_my_friends_vlist,
                new String[]{"friend_image","friend_name","friend_news"},
                new int[]{R.id.friend_image, R.id.friend_name, R.id.friend_news});
        ListView lt = (ListView)findViewById(R.id.friendslist);
        lt.setAdapter(adapter);
    }

    private List<Map<String,Object>> getNews(){
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("friend_image", R.drawable.man);
        map.put("friend_name", "harbin");
        map.put("friend_news", "好开心啊");
        list.add(map);

        map = new HashMap<>();
        map.put("friend_image", R.drawable.man);
        map.put("friend_name", "harbin");
        map.put("friend_news", "好开心啊");
        list.add(map);

        map = new HashMap<>();
        map.put("friend_image", R.drawable.man);
        map.put("friend_name", "harbin");
        map.put("friend_news", "好开心啊");
        list.add(map);

        map = new HashMap<>();
        map.put("friend_image", R.drawable.man);
        map.put("friend_name", "harbin");
        map.put("friend_news", "好开心啊");
        list.add(map);

        map = new HashMap<>();
        map.put("friend_image", R.drawable.man);
        map.put("friend_name", "harbin");
        map.put("friend_news", "好开心啊");
        list.add(map);

        return list;
    }

}

