package com.example.pay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminActivity extends AppCompatActivity {

    //列出一个activity的列表，将跳转用的activity信息填进去
    private static final activity[] content_activity = {
            new activity(SearchUserInfo.class),
            new activity(SearchUserOut.class),
            new activity(SearchPayInfo.class)
    };

    //定义一个activity类，比较方便保存activity信息
    private static class activity{
        private final Class<? extends android.app.Activity> activityClass;
        public activity(Class<? extends android.app.Activity> demoClass) {

            this.activityClass = demoClass;
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Log.d("test", "start activity");
        //用来保存需要的字符串信息
        List<Map<String, Object>> listitem = new ArrayList<Map<String, Object>>();
        //这是ListView显示用的字符串
        String[] content = new String[]{"   查询用户个人信息", "   查询用户出行记录", "   查询用户支付记录"};



        for (int i = 0; i < 3; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("content", content[i]);
            listitem.add(map);

        }

        //创建适配器
        // 第一个参数是上下文对象
        // 第二个是listitem
        // 第三个是指定每个列表项的布局文件
        // 第四个是指定Map对象中定义的两个键（这里通过字符串数组来指定）
        // 第五个是用于指定在布局文件中定义的id（也是用数组来指定）
        SimpleAdapter adapter = new SimpleAdapter(getApplicationContext()
                , listitem
                , R.layout.activity_content_item
                , new String[]{"content"}
                , new int[]{R.id.content_text});

        ListView listView = (ListView) findViewById(R.id.content);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View v, int index, long arg3) {
                onListItemClick(index);
            }
        });
    }

    void onListItemClick(int index){
        Intent intent = null;
        intent = new Intent(AdminActivity.this, content_activity[index].activityClass);
        this.startActivity(intent);
    }




}
