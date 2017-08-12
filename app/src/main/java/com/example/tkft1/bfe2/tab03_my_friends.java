package com.example.tkft1.bfe2;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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

        SimpleAdapter adapter = new SimpleAdapter(this, getNews(50), R.layout.tab03_my_friends_vlist,
                new String[]{"friend_image","friend_name","friend_news"},
                new int[]{R.id.friend_image, R.id.friend_name, R.id.friend_news});

        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                System.out.println(2);
                if(view instanceof ImageView && data instanceof String){
                    DownImage downImage = new DownImage((ImageView) view);
                    downImage.execute((String)data);
                    return true;
                }
                return false;
            }
        });

        ListView lt = (ListView)findViewById(R.id.friendslist);
        lt.setAdapter(adapter);
    }

    private List<Map<String,Object>> getNews(int n){
        List<Map<String, Object>> list = new ArrayList<>();

        Map<String, Object> map = new HashMap<>();
        map.put("friend_image", "http://v1.qzone.cc/avatar/201508/18/10/58/55d29f5377de2175.jpg%21200x200.jpg");
        map.put("friend_name", "habin");
        map.put("friend_news", "好啊");
        list.add(map);

        map = new HashMap<>();
        map.put("friend_image", R.drawable.man2);
        map.put("friend_name", "hain");
        map.put("friend_news", "好开心啊");
        list.add(map);

        map = new HashMap<>();
        map.put("friend_image", "http://img03.tooopen.com/images/20131102/sy_45238929299.jpg");
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
