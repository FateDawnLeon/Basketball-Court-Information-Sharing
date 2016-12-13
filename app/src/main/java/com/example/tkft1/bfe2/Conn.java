package com.example.tkft1.bfe2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

class Conn {
    private static Connection con;
    private static String driver = "com.mysql.jdbc.Driver";
    private static String username = "root";
    private static String password = "leonhitcx";
    private static String url = String.format("jdbc:mysql://localhost:3306/bcis?"
            + "user=%s&password=%s&useUnicode=true&characterEncoding=UTF8", username, password);



    public static Connection getConnection(){
        try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url);
            System.out.println("链接成功！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！");
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("链接失败！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！");
        }
        return con;
    }

    public static int excuteUpdate(String option) {
        try {
            Statement stmt = con.createStatement();
            int res = stmt.executeUpdate(option);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}

