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
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserInfo extends AppCompatActivity implements View.OnClickListener{
    private Handler mhandler;
    public TextView username;
    public TextView wallet;
    public TextView realname;
    public TextView person;
    public TextView state;
    public String name1;
    public String realname1;
    public  TextView record;
    public Button modify;
    public String mail;

    private boolean INFORMATION_STATE = false;
    private boolean DB_STATE = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        Intent i = getIntent();
        mail = i.getStringExtra("userid");

        username = (TextView) findViewById(R.id.username);
        realname = (TextView)findViewById(R.id.realname);
        wallet = findViewById(R.id.wallet);
        record = findViewById(R.id.record);
        person = findViewById(R.id.person);
        state = findViewById(R.id.state);
        modify = findViewById(R.id.modify);
        state.setOnClickListener(this);
        wallet.setOnClickListener(this);
        record.setOnClickListener(this);
        modify.setOnClickListener(this);
        person.setOnClickListener(this);
        HandlerThread thread = new HandlerThread("HT");
        thread.start();
        mhandler = new Handler(thread.getLooper());
        mhandler.post(update);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.wallet:
                Intent intent = new Intent(this,WalletActivity.class);
                intent.putExtra("userid", mail);
                startActivity(intent);
                break;
            case  R.id.record:
                Intent intent1 = new Intent(this,RecordActivity.class);
                intent1.putExtra("userid", mail);
                startActivity(intent1);
                break;
            case R.id.state: //接到健康码那里
//                HandlerThread thread = new HandlerThread("HT");
//                thread.start();
//                mhandler = new Handler(thread.getLooper());
//                mhandler.post(checkInformation);
                if(!DB_STATE)Toast.makeText(getApplicationContext(),"等待数据库加载",Toast.LENGTH_LONG).show();
                else{
                    if(!INFORMATION_STATE) Toast.makeText(getApplicationContext(),"请完善个人信息",Toast.LENGTH_LONG).show();
                    else{
                        Intent intent2 = new Intent(this,ScanActivity.class);
                        intent2.putExtra("userid", mail);
                        startActivity(intent2);
                    }
                }
                break;
            case R.id.person:
                Intent intent3 = new Intent(this,PersonalActivity.class);
                intent3.putExtra("userid", mail);
                startActivity(intent3);
                break;
            case R.id.modify:
                Intent intent4 = new Intent(this,ModifyActivity.class);
                intent4.putExtra("userid", mail);
                startActivity(intent4);
                mhandler.post(update);
                break;
            default:
                break;
        }
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
                rs = stmt.executeQuery();
                DB_STATE = true;

                while (rs.next()) {
                    name1=rs.getString("user_id");
                    realname1=rs.getString("name");
                    Log.d("username",name1);
                    Log.d("res", "1");
                    username.post(new Runnable() {

                        @Override
                        public void run() {
                            username.setText(name1);
                        }
                    });
                    realname.post(new Runnable() {

                        @Override
                        public void run() {
                            realname.setText("真实姓名："+realname1);
                        }
                    });

                    String idcard = rs.getString("idcard");
                    String phone = rs.getString("phone");
                    String healthId = rs.getString("health_id");
                    String balance = rs.getString("balance");
                    String pwd = rs.getString("pay_password");

                    Log.e("account",realname1 + "#" + idcard + "#"  + phone + "#" + healthId + "#" + balance + "#" + pwd);
                    if(realname1 != null && idcard != null && phone != null && healthId != null && balance != null && pwd != null)
                        INFORMATION_STATE = true;

                };
            } catch (SQLException e) {
                e.printStackTrace();
            }


        }
    };


}
