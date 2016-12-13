package com.example.tkft1.bfe2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DbHelper {
    private static Connection conn;
    private static final String driver = "com.mysql.jdbc.Driver";
    private static final String username = "root";
    private static final String password = "leonhitcx";
    private static final String url = "jdbc:mysql://10.0.2.2/bcis";

    public static void getConnection() {
        try {
            Class.forName(driver).newInstance();
            conn = DriverManager.getConnection(url,username,password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ResultSet excuteQuery(String sql) {
        ResultSet rs = null;
        try {
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    public static boolean excuteUpdate(String sql) {
        boolean flag = false;
        try {
            Statement stmt = conn.createStatement();
            int tag = stmt.executeUpdate(sql);
            if (tag > 0) {
                flag = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
    }

    public static void close() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

