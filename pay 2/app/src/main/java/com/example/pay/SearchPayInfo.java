package com.example.pay;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.View;
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

public class SearchPayInfo extends AppCompatActivity {
    private Handler mhandler;
    public EditText user_account;
    public EditText user_id;
    public List<String> consumption_id = new ArrayList<String>();
    public List<String> consumption_time = new ArrayList<String>();
    public List<String> consumption_place = new ArrayList<String>();
    public List<String> consumption_amount = new ArrayList<String>();
    public List<String> consumption_object = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_pay_info);
        user_account = findViewById(R.id.searchPayAccount);
        user_id = findViewById(R.id.searchPayIDCard);
    }

    public void search(View view) throws InterruptedException {
        HandlerThread thread = new HandlerThread("HT");
        thread.start();
        mhandler = new Handler(thread.getLooper());
        //这里的sqlrunnable就是我们之后要运行的数据库相关的语句，往下面可以看到
        //如果要写新的数据库语句，要再新建新的Runnable对象，并且进行post
        mhandler.post(searchInfo2);
        Thread.sleep(1000);

        List<Map<String, Object>> listitem = new ArrayList<Map<String, Object>>();
        //这是ListView显示用的字符串


        listitem.clear();


        for (int i = 0; i < consumption_id.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("consumption_id", "消费序列号："+consumption_id.get(i));
            map.put("consumption_time", "消费时间："+consumption_time.get(i));
            map.put("consumption_place", "消费地点："+consumption_place.get(i));
            map.put("consumption_amount", "消费金额："+consumption_amount.get(i));
            map.put("consumption_object", "消费对象："+consumption_object.get(i));
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
                , R.layout.user_pay_content
                , new String[]{"consumption_id", "consumption_time", "consumption_place", "consumption_amount", "consumption_object"}
                , new int[]{R.id.consumption_id, R.id.consumption_time, R.id.consumption_place, R.id.consumption_amount, R.id.consumption_object});

        ListView listView = (ListView) findViewById(R.id.userPay);

        listView.setAdapter(adapter);
    }

    public void searchIDCard(View view) throws InterruptedException {
        HandlerThread thread = new HandlerThread("HT");
        thread.start();
        mhandler = new Handler(thread.getLooper());
        //这里的sqlrunnable就是我们之后要运行的数据库相关的语句，往下面可以看到
        //如果要写新的数据库语句，要再新建新的Runnable对象，并且进行post
        mhandler.post(searchID2);
        Thread.sleep(1000);

        List<Map<String, Object>> listitem = new ArrayList<Map<String, Object>>();
        //这是ListView显示用的字符串


        listitem.clear();


        for (int i = 0; i < consumption_id.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("consumption_id", "消费序列号："+consumption_id.get(i));
            map.put("consumption_time", "消费时间："+consumption_time.get(i));
            map.put("consumption_place", "消费地点："+consumption_place.get(i));
            map.put("consumption_amount", "消费金额："+consumption_amount.get(i));
            map.put("consumption_object", "消费对象："+consumption_object.get(i));
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
                , R.layout.user_pay_content
                , new String[]{"consumption_id", "consumption_time", "consumption_place", "consumption_amount", "consumption_object"}
                , new int[]{R.id.consumption_id, R.id.consumption_time, R.id.consumption_place, R.id.consumption_amount, R.id.consumption_object});

        ListView listView = (ListView) findViewById(R.id.userPay);

        listView.setAdapter(adapter);
    }
    //数据库语句，反正你们看就能看懂大概意思我就不解释了，有啥问题再问吧
    Runnable searchInfo2 = new Runnable() {
        @Override
        public void run() {
            Connection conn=null;
            PreparedStatement stmt=null;
            ResultSet rs = null;
            //DBConnection.linkMysql();

            try {
                conn = DriverManager.getConnection(DBConnection.url,DBConnection.user,DBConnection.pwd);
                System.out.println("连接数据库成功！！！！！！");
                stmt= conn.prepareStatement("Select * from consumption where user_id='"+(user_account.getText().toString())+"';");
                rs = stmt.executeQuery();
                consumption_id.clear();
                consumption_time.clear();
                consumption_place.clear();
                consumption_amount.clear();
                consumption_object.clear();
                int count = 0;
                while (rs.next()) {
                    count++;
                    consumption_id.add(rs.getString("consumption_id"));
                    //Log.d("a", rs.getString("name"));
                    consumption_time.add(rs.getString("consumption_time"));
                    //Log.d("a", rs.getString("idcard"));
                    consumption_place.add(rs.getString("consumption_place"));
                    //Log.d("a", rs.getString("phone"));
                    consumption_amount.add(rs.getString("consumption_amount"));
                    //Log.d("a", rs.getString("health_id"));
                    consumption_object.add(rs.getString("consumption_object"));
                    //Log.d("a", rs.getString("health_id"));

                };
                if(count == 0){
                    Toast.makeText(getApplicationContext(),"账号不存在",Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"查询成功",Toast.LENGTH_LONG).show();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }


        }
    };

    Runnable searchID2 = new Runnable() {
        @Override
        public void run() {
            Connection conn=null;
            PreparedStatement stmt=null;
            ResultSet rs = null;
            //DBConnection.linkMysql();

            try {
                conn = DriverManager.getConnection(DBConnection.url,DBConnection.user,DBConnection.pwd);
                System.out.println("连接数据库成功！！！！！！");

                stmt= conn.prepareStatement("Select * from p_information where idcard='"+(user_id.getText().toString())+"';");
                rs = stmt.executeQuery();
                rs.next();
                String user_ac = "";
                if(rs.next()){
                    user_ac = rs.getString("user_id");
                }

                stmt= conn.prepareStatement("Select * from consumption where user_id='"+(user_ac)+"';");
                rs = stmt.executeQuery();
                consumption_id.clear();
                consumption_time.clear();
                consumption_place.clear();
                consumption_amount.clear();
                consumption_object.clear();
                int count = 0;
                while (rs.next()) {
                    count++;
                    consumption_id.add(rs.getString("consumption_id"));
                    //Log.d("a", rs.getString("name"));
                    consumption_time.add(rs.getString("consumption_time"));
                    //Log.d("a", rs.getString("idcard"));
                    consumption_place.add(rs.getString("consumption_place"));
                    //Log.d("a", rs.getString("phone"));
                    consumption_amount.add(rs.getString("consumption_amount"));
                    //Log.d("a", rs.getString("health_id"));
                    consumption_object.add(rs.getString("consumption_object"));
                    //Log.d("a", rs.getString("health_id"));

                };
                if(count == 0){
                    Toast.makeText(getApplicationContext(),"账号不存在",Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"查询成功",Toast.LENGTH_LONG).show();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }


        }
    };
}
