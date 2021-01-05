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

public class SearchUserOut extends AppCompatActivity {
    private Handler mhandler;
    public EditText user_account;
    public EditText user_id;
    public List<String> travel_id = new ArrayList<String>();
    public List<String> travel_time = new ArrayList<String>();
    public List<String> travel_place = new ArrayList<String>();
    public List<String> health_state = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user_out);
        user_account = findViewById(R.id.searchOutAccount);
        user_id= findViewById(R.id.searchOutIDCard);
    }

    public void search(View view) throws InterruptedException {
        HandlerThread thread = new HandlerThread("HT");
        thread.start();
        mhandler = new Handler(thread.getLooper());
        //这里的sqlrunnable就是我们之后要运行的数据库相关的语句，往下面可以看到
        //如果要写新的数据库语句，要再新建新的Runnable对象，并且进行post
        mhandler.post(searchInfo1);

        Thread.sleep(1000);
        List<Map<String, Object>> listitem = new ArrayList<Map<String, Object>>();
        //这是ListView显示用的字符串


        listitem.clear();


        for (int i = 0; i < travel_id.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("travel_id", "出行记录序列号："+travel_id.get(i));
            map.put("travel_time", "出行时间："+travel_time.get(i));
            map.put("travel_place", "出行地点："+travel_place.get(i));
            map.put("health_state", "健康状态："+health_state.get(i));
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
                , R.layout.user_travel_content
                , new String[]{"travel_id", "travel_time", "travel_place", "health_state"}
                , new int[]{R.id.travel_id, R.id.travel_time, R.id.travel_place, R.id.health_state});

        ListView listView = (ListView) findViewById(R.id.userOut);

        listView.setAdapter(adapter);
    }

    public void searchIDCard(View view) throws InterruptedException {
        HandlerThread thread = new HandlerThread("HT");
        thread.start();
        mhandler = new Handler(thread.getLooper());
        //这里的sqlrunnable就是我们之后要运行的数据库相关的语句，往下面可以看到
        //如果要写新的数据库语句，要再新建新的Runnable对象，并且进行post
        mhandler.post(searchID1);
        Thread.sleep(1000);

        List<Map<String, Object>> listitem = new ArrayList<Map<String, Object>>();
        //这是ListView显示用的字符串


        listitem.clear();


        for (int i = 0; i < travel_id.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("travel_id", "出行记录序列号："+travel_id.get(i));
            map.put("travel_time", "出行时间："+travel_time.get(i));
            map.put("travel_place", "出行地点："+travel_place.get(i));
            map.put("health_state", "健康状态："+health_state.get(i));
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
                , R.layout.user_travel_content
                , new String[]{"travel_id", "travel_time", "travel_place", "health_state"}
                , new int[]{R.id.travel_id, R.id.travel_time, R.id.travel_place, R.id.health_state});

        ListView listView = (ListView) findViewById(R.id.userOut);

        listView.setAdapter(adapter);
    }
    //数据库语句，反正你们看就能看懂大概意思我就不解释了，有啥问题再问吧
    Runnable searchInfo1 = new Runnable() {
        @Override
        public void run() {
            Connection conn=null;
            PreparedStatement stmt=null;
            ResultSet rs = null;
            //DBConnection.linkMysql();

            try {
                conn = DriverManager.getConnection(DBConnection.url,DBConnection.user,DBConnection.pwd);
                System.out.println("连接数据库成功！！！！！！");
                stmt= conn.prepareStatement("Select * from travel where user_id='"+(user_account.getText().toString())+"';");
                rs = stmt.executeQuery();
                travel_id.clear();
                travel_time.clear();
                travel_place.clear();
                health_state.clear();
                int count = 0;
                while (rs.next()) {
                    count++;
                    travel_id.add(rs.getString("travel_id"));
                    //Log.d("a", rs.getString("name"));
                    travel_time.add(rs.getString("travel_time"));
                    //Log.d("a", rs.getString("idcard"));
                    travel_place.add(rs.getString("travel_place"));
                    //Log.d("a", rs.getString("phone"));
                    health_state.add(rs.getString("health_state"));
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
    //数据库语句，反正你们看就能看懂大概意思我就不解释了，有啥问题再问吧
    Runnable searchID1 = new Runnable() {
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
                //Log.d("//", "Select * from p_information where idcard='"+(user_id.getText().toString())+"';");
                rs = stmt.executeQuery();

                String user_ac = "";
                if(rs.next()){
                    user_ac = rs.getString("user_id");
                }


                stmt= conn.prepareStatement("Select * from travel where user_id='"+(user_ac)+"';");

                rs = stmt.executeQuery();

                travel_id.clear();
                travel_time.clear();
                travel_place.clear();
                health_state.clear();
                int count = 0;
                while (rs.next()) {
                    count++;
                    travel_id.add(rs.getString("travel_id"));
                    //Log.d("a", rs.getString("name"));
                    travel_time.add(rs.getString("travel_time"));
                    //Log.d("a", rs.getString("idcard"));
                    travel_place.add(rs.getString("travel_place"));
                    //Log.d("a", rs.getString("phone"));
                    health_state.add(rs.getString("health_state"));
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
