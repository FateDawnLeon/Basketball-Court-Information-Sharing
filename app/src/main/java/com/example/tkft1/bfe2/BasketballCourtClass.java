package com.example.tkft1.bfe2;

import com.baidu.mapapi.model.LatLng;

/**
 * Created by tkft1 on 2016/11/27.
 */

public class BasketballCourtClass {
    public String CourtName;
    public LatLng Location;
    public String City;
    public String Address;
    public String Distance;

    @Override
    public String toString() {
        return CourtName + City + Address + Distance;
    }
}
