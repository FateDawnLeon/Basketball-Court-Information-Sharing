package com.example.tkft1.bfe2;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.example.tkft1.bfe2.R.id.username;


public class tab03_my_register extends Activity implements ViewStub.OnClickListener{
    private EditText editText_username;
    private EditText editText_password1;
    private EditText editText_password2;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab03_my_register);
    }

    @Override
    public void onClick(View view) {
        editText_username = (EditText) findViewById(username);
        editText_password1 = (EditText) findViewById(R.id.password);
        editText_password2 = (EditText) findViewById(R.id.confirm_password);
        final String username = editText_username.getText().toString();
        final String password = editText_password1.getText().toString();
        String confirm_password = editText_password2.getText().toString();

        if (!username.isEmpty() && !password.isEmpty()) {
            if (password.equals(confirm_password)) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        DbHelper.getConnection();
                        String sql = "select * from userinfo where username='" + username +"';";
                        ResultSet rs = DbHelper.excuteQuery(sql);
                        try {
                            if (rs.next()) {
//                                editText_username.setText("");
//                                Toast.makeText(getApplicationContext(), "此用戶名已存在！", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                String sql2 = String.format("insert into userinfo(username,password) values('%s','%s')",username,password);
                                boolean flag = DbHelper.excuteUpdate(sql2);
                                if (flag) {
                                    System.out.println("insert success!");
                                }
                                else {
                                    System.out.println("insert failure!");
                                }
//                                editText_username.setText("");
//                                editText_password1.setText("");
//                                editText_password2.setText("");
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        DbHelper.close();
                    }
                }).start();
            }
            else {
                editText_password1.setText("");
                editText_password2.setText("");
                Toast.makeText(getApplicationContext(), "確認密碼與輸入密碼不符！", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "用戶名和密碼不能為空！", Toast.LENGTH_SHORT).show();
        }
    }
}