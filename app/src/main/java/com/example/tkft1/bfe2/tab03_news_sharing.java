package com.example.tkft1.bfe2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class tab03_news_sharing extends Activity implements View.OnClickListener {

    EditText editText;
    Intent intent;
    ArrayList<String> information = new ArrayList<>();
    String address;
    String username = "student1";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab03_news_sharing);
        intent = getIntent();
        information = intent.getStringArrayListExtra("com.example.tkft1.bfe2.information");

        username = information.get(0);
        address = information.get(1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share_news_btn:
                editText = (EditText) findViewById(R.id.editText);
                final String remark = editText.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        int id = 0;
                        DbHelper.getConnection();
                        String sql1 = null;
                        try {
                            sql1 = String.format("select * from basketballcourt where address='%s';", URLEncoder.encode(address,"UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        ResultSet rs = DbHelper.excuteQuery(sql1);
                        try {
                            if (rs.next()) {
                                id = rs.getInt("id");
                            }
                            else {
                                System.out.println("No neareast court exist in the table!");
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        String sql3 = "select * from userinfo where username='"+username+"';";
                        ResultSet rs3 = DbHelper.excuteQuery(sql3);
                        int userid = 0;
                        try {
                            if (rs3.next()) {
                                userid = rs3.getInt("id");
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        String courtid = id+"";
                        try {
                            String sql2 = String.format("insert into dynamics values('%s',%s,%s,'%s');",username,userid+"",courtid, URLEncoder.encode(remark,"UTF-8"));
                            boolean f = DbHelper.excuteUpdate(sql2);
                            if (f) {
                                System.out.println("INSERT SUCCESS!");
                            }
                            else {
                                System.out.println("INSERT FAILURE!");
                            }
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        DbHelper.close();
                    }
                }).start();

                editText.setText("");
                Toast.makeText(getApplicationContext(), "发布成功", Toast.LENGTH_SHORT).show();

                break;
            default:
                break;
        }
    }
}
