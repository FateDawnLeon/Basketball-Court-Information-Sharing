package com.example.tkft1.bfe2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewsList {
    static List<Map<String, Object>> list = new ArrayList<>();

    public List<Map<String, Object>> getlist(){
        return list;
    }

    public List<Map<String,Object>> getNews(){
        Map<String, Object> map = new HashMap<>();
        map.put("user", "godchen");
        map.put("position", "harbin");
        map.put("introduction", "好开心啊");
        list.add(map);

        map = new HashMap<>();
        map.put("user", "godliu");
        map.put("position", "harbin");
        map.put("introduction", "好开心啊");
        list.add(map);

        map = new HashMap<>();
        map.put("user", "godzhang");
        map.put("position", "harbin");
        map.put("introduction", "好开心啊");
        list.add(map);

        for(int i=0; i<100; i++) {
            map = new HashMap<>();
            map.put("user", "#");
            map.put("position", "#");
            map.put("introduction", "###");
            list.add(map);
        }
        return list;
    }
}
