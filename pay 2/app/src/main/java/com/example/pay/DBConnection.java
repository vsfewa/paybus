package com.example.pay;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

//目前这个文件只做存储ip，用户和密码只用，没有其他用途，里面不知道干嘛的部分不要研究，我也不是很清楚（
public class DBConnection {
    private static final String driver = "com.mysql.jdbc.Driver";
    //    private static final String url = "jdbc:mysql://192.168.43.173:3306/test?useSSL=true&serverTimezone=GMT";
    public static final String url = "jdbc:mysql://120.55.192.180:3306/pay?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&failOverReadOnly=false";
    public static final String user = "root";
    public static final String pwd = "123456";

    public static void linkMysql() {
        Connection conn=null;
        PreparedStatement stmt=null;
        ResultSet rs = null;
        try {
            Class.forName(driver).newInstance();
            System.out.println("驱动加载成功！！！！！");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        try{


        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if(conn!=null){
                try {
                    conn.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
