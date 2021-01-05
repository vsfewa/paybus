package com.example.pay;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
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

public class SearchUserInfo extends AppCompatActivity {
    private Handler mhandler;
    public EditText user_account;
    public EditText user_id;
    public List<String> name = new ArrayList<String>();
    public List<String> idcard = new ArrayList<String>();
    public List<String> phone = new ArrayList<String>();
    public List<String> healthid = new ArrayList<String>();
    public List<String> balance = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user_info);
        user_account = findViewById(R.id.searchAccount1);
        user_id = findViewById(R.id.searchIDCard);





    }

    public void search(View view) throws InterruptedException {
        HandlerThread thread = new HandlerThread("HT");
        thread.start();
        mhandler = new Handler(thread.getLooper());
        //这里的sqlrunnable就是我们之后要运行的数据库相关的语句，往下面可以看到
        //如果要写新的数据库语句，要再新建新的Runnable对象，并且进行post
        mhandler.post(searchInfo);
        Thread.sleep(1000);

        List<Map<String, Object>> listitem = new ArrayList<Map<String, Object>>();
        //这是ListView显示用的字符串


        listitem.clear();


        for (int i = 0; i < name.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("name", "姓名："+name.get(i));
            map.put("idcard", "身份证号："+idcard.get(i));
            map.put("phone", "电话号码："+phone.get(i));
            map.put("healthid", "健康码序列号："+healthid.get(i));
            map.put("balance", "账户余额："+balance.get(i));

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
                , R.layout.user_info_content
                , new String[]{"name", "idcard", "phone", "healthid", "balance"}
                , new int[]{R.id.name, R.id.idcard, R.id.phone, R.id.healthid, R.id.balance});

        ListView listView = (ListView) findViewById(R.id.userInfo);

        listView.setAdapter(adapter);
    }

    public void searchIDCard(View view) throws InterruptedException {
        HandlerThread thread = new HandlerThread("TH");
        thread.start();
        mhandler = new Handler(thread.getLooper());
        //这里的sqlrunnable就是我们之后要运行的数据库相关的语句，往下面可以看到
        //如果要写新的数据库语句，要再新建新的Runnable对象，并且进行post
        mhandler.post(searchID);
        Thread.sleep(1000);

        List<Map<String, Object>> listitem = new ArrayList<Map<String, Object>>();
        //这是ListView显示用的字符串


        listitem.clear();


        for (int i = 0; i < name.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("name", "姓名："+name.get(i));
            map.put("idcard", "身份证号："+idcard.get(i));
            map.put("phone", "电话号码："+phone.get(i));
            map.put("healthid", "健康码序列号："+healthid.get(i));
            map.put("balance", "账户余额："+balance.get(i));

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
                , R.layout.user_info_content
                , new String[]{"name", "idcard", "phone", "healthid", "balance"}
                , new int[]{R.id.name, R.id.idcard, R.id.phone, R.id.healthid, R.id.balance});

        ListView listView = (ListView) findViewById(R.id.userInfo);

        listView.setAdapter(adapter);
    }

    //数据库语句，反正你们看就能看懂大概意思我就不解释了，有啥问题再问吧
    Runnable searchInfo = new Runnable() {
        @Override
        public void run() {
            Connection conn=null;
            PreparedStatement stmt=null;
            ResultSet rs = null;
            //DBConnection.linkMysql();

            try {
                conn = DriverManager.getConnection(DBConnection.url,DBConnection.user,DBConnection.pwd);
                System.out.println("连接数据库成功！！！！！！");
                stmt= conn.prepareStatement("Select * from p_information where user_id='"+(user_account.getText().toString())+"';");
                rs = stmt.executeQuery();

                int count = 0;
                name.clear();
                idcard.clear();
                phone.clear();
                healthid.clear();
                balance.clear();

                while (rs.next()) {
                    count++;
                    name.add(rs.getString("name"));
                    Log.d("a", rs.getString("name"));
                    idcard.add(rs.getString("idcard"));
                    Log.d("a", rs.getString("idcard"));
                    phone.add(rs.getString("phone"));
                    Log.d("a", rs.getString("phone"));
                    healthid.add(rs.getString("health_id"));
                    Log.d("a", rs.getString("health_id"));
                    balance.add(rs.getString("balance"));
                    Log.d("a", rs.getString("balance"));


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
    Runnable searchID = new Runnable() {
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
                name.clear();
                idcard.clear();
                phone.clear();
                healthid.clear();
                balance.clear();
                int count = 0;
                while (rs.next()) {
                    count++;
                    name.add(rs.getString("name"));
                    Log.d("a", rs.getString("name"));
                    idcard.add(rs.getString("idcard"));
                    Log.d("a", rs.getString("idcard"));
                    phone.add(rs.getString("phone"));
                    Log.d("a", rs.getString("phone"));
                    healthid.add(rs.getString("health_id"));
                    Log.d("a", rs.getString("health_id"));
                    balance.add(rs.getString("balance"));
                    Log.d("a", rs.getString("balance"));


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
