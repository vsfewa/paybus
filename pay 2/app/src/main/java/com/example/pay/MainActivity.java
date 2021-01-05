package com.example.pay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;


import com.mysql.jdbc.JDBC4Connection;

//这是一个主函数，这里暂时什么都没做，后续可能把这里做成一个启动动画的显示页面
//即通过函数控制在此处停留数秒，同时显示一个logo即可
//目前页面跳转需要由onclick完成

public class MainActivity extends AppCompatActivity {
    //延迟三秒
    private final int SPLASH_DISPLAY_LENGHT = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                MainActivity.this.startActivity(intent);
                MainActivity.this.finish();
            }
        },SPLASH_DISPLAY_LENGHT);

    }



}
