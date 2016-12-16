package com.example.tkft1.bfe2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NewsList extends AsyncTask<String, Integer, Boolean>{

    static List<Map<String, Object>> list = new ArrayList<>();
    private TabHost th;
    private LayoutInflater i;
    private TabHost.TabSpec tab01;
    private Map<String, Object> map;
    DataFinishListener dataFinishListener;



    public NewsList(TabHost th,LayoutInflater i,TabHost.TabSpec tab01){
        this.th = th;
        this.i = i;
        this.tab01 = tab01;
    }

    public NewsList(){}

    public void setFinishListener(DataFinishListener dataFinishListener){
        this.dataFinishListener = dataFinishListener;
    }

    public List<Map<String, Object>> getlist(){
        return list;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        DbHelper.getConnection();
        String sql = "select * from dynamics";
        ResultSet rs = DbHelper.excuteQuery(sql);
        try {
            list.clear();
            while (rs.next()){
                String courtname = "篮球场";
                String username = rs.getString("username");
                String remark = rs.getString("remark");
                int courtid = rs.getInt("courtid");
                String sql2 = String.format("select * from basketballcourt where id=%s",courtid+"");
                ResultSet rs2 = DbHelper.excuteQuery(sql2);
                try {
                    if (rs2.next()) {
                        courtname = rs2.getString("name");
                    }
                } catch (SQLException e)  {
                    e.printStackTrace();
                }
                map = new HashMap<>();
                map.put("user", username);
                map.put("position", URLDecoder.decode(courtname,"UTF-8"));
                map.put("introduction", URLDecoder.decode(remark,"UTF-8"));
                list.add(map);
            }
            return true;
        }
        catch (SQLException e)  {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    protected void onPostExecute(Boolean result){
        if (result){
            dataFinishListener.dataFinishSuccessfully(result);
        }
    }

    public static interface DataFinishListener{
        void dataFinishSuccessfully(Boolean data);
    }
}
