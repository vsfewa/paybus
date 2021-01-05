package com.example.pay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WalletActivity extends AppCompatActivity implements View.OnClickListener {
    public TextView balance;
    public TextView  phone;
    private Handler mhandler;
    public Button buy;
    //线程标志
    public int thread1 = 0;
    public double money;
    public String phonenumber;
    public String mail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        Intent i = getIntent();
        mail = i.getStringExtra("userid");
        System.out.println("dadda"+mail);
        balance = findViewById(R.id.cashmoney);
        phone =  findViewById(R.id.phone);
        buy = findViewById(R.id.buy);
        buy.setOnClickListener(this);
        thread1 = 1;
        HandlerThread thread = new HandlerThread("HT");
        thread.start();
        mhandler = new Handler(thread.getLooper());
        mhandler.post(update);
        balance.setText(money+"元");
        phone.setText(phonenumber);
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
                // 对接后使用
                stmt= conn.prepareStatement("Select * from p_information where user_id='" + mail + "'");
                //stmt= conn.prepareStatement("Select * from p_information ");
                rs = stmt.executeQuery();
                while (rs.next()) {
                    money=rs.getDouble("balance");
                    phonenumber=rs.getString("phone");
                    Log.d("res", "1");
                    balance.post(new Runnable() {

                        @Override
                        public void run() {
                            balance.setText(money+"元");
                        }
                    });
                    phone.post(new Runnable() {

                        @Override
                        public void run() {
                            phone.setText(phonenumber);
                        }
                    });
                };
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    };
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.buy:
                Intent intent = new Intent(this,ChargeActivity.class);
                intent.putExtra("userid", mail);
                intent.putExtra("alipay",phonenumber);
                startActivity(intent);
                break;

        }
    }
    @Override
    protected void onDestroy() {
        if(thread1>0) {
            mhandler.removeCallbacks(update);
        }
        super.onDestroy();
    }

}
