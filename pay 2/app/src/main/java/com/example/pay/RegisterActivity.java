package com.example.pay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    public Handler mhandler;
    public EditText account;
    public EditText password;
    public EditText repeat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    //标记是否调用数据库
    public int used=0;
    //定义邮箱正则表达式
    public static final String REGEX_EMAL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
    public void onclick(View view){
        //获取文本框对象
        account = (EditText)findViewById(R.id.accountInput);
        password = (EditText)findViewById(R.id.passwordInput);
        repeat = (EditText)findViewById(R.id.repeatInput);


        //获取密码长度
        int length = password.getText().toString().length();

        //判断两次输入的密码是否相同
        if (!(password.getText().toString().equals(repeat.getText().toString()))){
            Toast.makeText(getApplicationContext(),"密码不一致",Toast.LENGTH_LONG).show();
        }
        else if(!(Pattern.matches(REGEX_EMAL,account.getText().toString()))){
            Toast.makeText(getApplicationContext(),"邮箱格式不正确",Toast.LENGTH_LONG).show();
        }
        else if(length<6){
            Toast.makeText(getApplicationContext(),"密码太短，请输入6-16位密码",Toast.LENGTH_LONG).show();
        }
        else if(length>16){
            Toast.makeText(getApplicationContext(),"密码太长，请输入6-16位密码",Toast.LENGTH_LONG).show();
        }
        else{
            used = 1;
            //Toast.makeText(getApplicationContext(),"密码一致",Toast.LENGTH_LONG).show();
            HandlerThread thread = new HandlerThread("HT");
            thread.start();
            mhandler = new Handler(thread.getLooper());
            mhandler.post(sqlRun);
        }
    }

    Runnable sqlRun = new Runnable() {
        @Override
        public void run() {
            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;
            int flag = -1;
            String accountS;
            String passwordS;
            accountS = account.getText().toString();
            passwordS = password.getText().toString();
            try {
                conn = DriverManager.getConnection(DBConnection.url,DBConnection.user,DBConnection.pwd);
                System.out.println("数据库连接成功！");
                String sql = "insert into account values (" + "'"+accountS+"'" + "," + "'"+passwordS+"'" + ",'user');";
                stmt = conn.prepareStatement(sql);

                //rs = stmt.executeQuery();
                //标识插入是否成功
                flag = stmt.executeUpdate();
                if(flag>0){
                    Toast.makeText(getApplicationContext(),"注册成功",Toast.LENGTH_LONG).show();
                    sql = "insert into p_information values('" + accountS + "', null, null, null, null, 0.0, null);";
                    stmt = conn.prepareStatement(sql);
                    stmt.executeUpdate();
                }else{
                    Toast.makeText(getApplicationContext(),"账号已存在",Toast.LENGTH_LONG).show();
                }
            } catch (SQLException e) {
                //未成功，表示账号存在
                Toast.makeText(getApplicationContext(),"账号已存在",Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    };



}