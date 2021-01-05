package com.example.pay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonalActivity extends AppCompatActivity {
    private Handler mhandler;
    public TextView Rname;
    public TextView idcard;
    public TextView health;
    public TextView alipay;
    public String id;
    public String name;
    public String healthid;
    public String account;
    public String mail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        Intent i = getIntent();
        mail = i.getStringExtra("userid");
        Rname =findViewById(R.id.Rname);
        idcard = findViewById(R.id.idcard);
        health = findViewById(R.id.health);
        alipay =findViewById(R.id.alipay);
        HandlerThread thread = new HandlerThread("HT");
        thread.start();
        mhandler = new Handler(thread.getLooper());
        mhandler.post(update);
    }
    Runnable update = new Runnable() {
        @Override
        public void run() {
            Connection conn=null;
            PreparedStatement stmt=null;
            ResultSet rs = null;
            //DBConnection.linkMysql();

            try {
                conn = DriverManager.getConnection(DBConnection.url,DBConnection.user,DBConnection.pwd);
                System.out.println("连接数据库成功！！！！！！");
                // 对接后使用，其他activity也是这样
                stmt= conn.prepareStatement("Select * from p_information where user_id='" + mail + "'");
                //stmt= conn.prepareStatement("Select * from p_information ");
                rs = stmt.executeQuery();
                while (rs.next()) {
                    name=rs.getString("name");
                    id=rs.getString("idcard");
                    healthid=rs.getString("health_id");
                    account=rs.getString("phone");
                    Log.d("username","name");
                    Log.d("res", "1");
                    Rname.post(new Runnable() {

                        @Override
                        public void run() {
                            Rname.setText(name);
                        }
                    });
                    idcard.post(new Runnable() {

                        @Override
                        public void run() {
                            idcard.setText(id);
                        }
                    });

                    health.post(new Runnable() {

                        @Override
                        public void run() {
                            health.setText(healthid);
                        }
                    });
                    alipay.post(new Runnable() {

                        @Override
                        public void run() {
                            alipay.setText(account);
                        }
                    });
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }


        }
    };
}
