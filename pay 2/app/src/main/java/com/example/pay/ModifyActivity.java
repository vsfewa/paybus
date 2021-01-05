package com.example.pay;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ModifyActivity extends AppCompatActivity implements View.OnClickListener{
    private Handler mhandler;
    public EditText newname;
    public EditText newid;
    public EditText newhealth;
    public EditText newpay;
    public Button check;
    public  Button back;
    public String mail;
    public int used =0;
    //@SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);
        Intent i = getIntent();
        mail = i.getStringExtra("userid");
        newname = findViewById(R.id.newname);
        newid = findViewById(R.id.newid);
        newhealth = findViewById(R.id.newhealth);
        newpay = findViewById(R.id.newpay);
        check = (Button) findViewById(R.id.check);
        back = (Button)findViewById(R.id.back);
        back.setOnClickListener(this);
    }
    public void modify(View v){
        used=1;
        HandlerThread thread = new HandlerThread("HT");
        thread.start();
        mhandler = new Handler(thread.getLooper());
        mhandler.post(newinfo);
    }

    Runnable newinfo = new Runnable() {
        @Override
        public void run() {
            Connection conn=null;
            PreparedStatement stmt=null;
            ResultSet rs = null;
            //DBConnection.linkMysql();

            try {
                conn = DriverManager.getConnection(DBConnection.url,DBConnection.user,DBConnection.pwd);
                System.out.println("连接数据库成功！！！！！！");

                stmt = conn.prepareStatement("UPDATE p_information SET pay_password = ? WHERE user_id = ?");
                stmt.setString(1,"123456");
                stmt.setString(2, mail);
                stmt.executeUpdate();

                if(newname.length()!=0) {
                    Log.d("res","4a4d5d5a5d4");
                    String newreal=newname.getText().toString();
                    stmt = conn.prepareStatement("update p_information set name='" + newreal + "'where user_id='" + mail + "'");
                    int flag = stmt.executeUpdate();
                    if(flag>0){
                        Toast.makeText(getApplicationContext(),"修改成功",Toast.LENGTH_LONG).show();
                    }
                }
                if(newid.length()!=0) {
                    Log.d("res","4a4d5d5a5d4");
                    String newidcard=newid.getText().toString();
                    stmt = conn.prepareStatement("update p_information set idcard='" + newidcard + "'where user_id='" + mail + "'");
                    int flag = stmt.executeUpdate();
                    if(flag>0){
                        Toast.makeText(getApplicationContext(),"修改成功",Toast.LENGTH_LONG).show();
                    }
                }
                if(newhealth.length()!=0) {
                    String newhealthid=newhealth.getText().toString();
                    stmt = conn.prepareStatement("update p_information set health_id='" + newhealthid + "'where user_id='" + mail + "'");
                    int flag = stmt.executeUpdate();
                    if(flag>0){
                        Toast.makeText(getApplicationContext(),"修改成功",Toast.LENGTH_LONG).show();
                    }
                }
                if(newpay.length()!=0) {
                    String newphone=newpay.getText().toString();
                    stmt = conn.prepareStatement("update p_information set phone='" + newphone + "'where user_id='" + mail + "'");
                    int flag = stmt.executeUpdate();
                    if(flag>0){
                        Toast.makeText(getApplicationContext(),"修改成功",Toast.LENGTH_LONG).show();
                    }
                }
                Intent intent = new Intent(ModifyActivity.this,UserInfo.class);
                intent.putExtra("userid", mail);
                startActivity(intent);
            } catch (SQLException e) {
                e.printStackTrace();
            }


        }
    };
    protected void onDestroy() {

        if(used>0)
            mhandler.removeCallbacks(newinfo);
        super.onDestroy();

    }
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.back:
                Intent intent = new Intent(this,UserInfo.class);
                intent.putExtra("userid", mail);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
    }
}
