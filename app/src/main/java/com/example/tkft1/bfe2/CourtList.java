package com.example.tkft1.bfe2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourtList {
    static List<Map<String, Object>> list = new ArrayList<>();

    public List<Map<String, Object>> getlist(){
        return list;
    }

    public List<Map<String,Object>> getCourt(ArrayList<BasketballCourtClass> ar){
        Map<String, Object> map = new HashMap<>();

        for(int i=0; i<ar.size(); i++) {
            map = new HashMap<>();
            map.put("court_name", ar.get(i).CourtName);
            map.put("court_distance", ar.get(i).Distance);
            map.put("court_position", ar.get(i).Address);
            list.add(map);
        }


        return list;
    }
}
