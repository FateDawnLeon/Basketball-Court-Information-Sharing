package com.example.tkft1.bfe2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TabHost;

import java.io.InputStream;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login extends AsyncTask<String, Integer, Boolean>{
    static private boolean islogin = false;

    private TabHost th;
    private LayoutInflater i;
    private TabHost.TabSpec tab03;

    public Login(TabHost th,LayoutInflater i,TabHost.TabSpec tab03){
        this.th = th;
        this.i = i;
        this.tab03 = tab03;
    }

    public static boolean islogin() {
        return islogin;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        DbHelper.getConnection();
        String sql = "select * from userinfo where username='"+params[0]+"' and password='"+params[1]+"';";
        ResultSet rs = DbHelper.excuteQuery(sql);
        try {
            if(rs.next()) {
                islogin = true;
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        DbHelper.close();
        islogin = false;
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result){
        if (result){
            th.setCurrentTab(1);
            i.inflate(R.layout.tab03_my, th.getTabContentView());
            tab03.setContent(R.id.my);
            th.setCurrentTab(2);
        }
    }
}
