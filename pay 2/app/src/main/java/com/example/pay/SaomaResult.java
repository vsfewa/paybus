package com.example.pay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class SaomaResult extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saoma_result);
        //获取传递的intent
        Intent intent = getIntent();
        //获取intent中的info
        String info = intent.getStringExtra("info");

        //将一整串String分割为字符串数组
        //0 => user_id; 1 => name; 2 => idcard; 3 => phone; 4 => health_id; 5 => balance;
        String[] arr_info = info.split("#");

        //加载数据
        loadData(arr_info);
    }
    public void loadData(String[] arr_info){
        //获取信息TextView
        TextView nameView = findViewById(R.id.names);
        TextView idcardView = findViewById(R.id.idcards);
        TextView phoneView = findViewById(R.id.phones);
        TextView health_idView = findViewById(R.id.health_ids);

        //加载数据
        nameView.setText(arr_info[1]);
        idcardView.setText(arr_info[2]);
        phoneView.setText(arr_info[3]);
        health_idView.setText(arr_info[4]);
    }
}
