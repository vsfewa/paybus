package com.example.pay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.widget.ListView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RecordActivity extends AppCompatActivity {
    public List<Record> RecordList = new ArrayList<Record>(100);
    private Handler mhandler;
    public ListView listView;
    public RecordAdapter adapter;
    public int thread2 = 0;
    public String mail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        Intent i = getIntent();
        mail = i.getStringExtra("userid");
        listView = (ListView) findViewById(R.id.list_view);
        HandlerThread thread = new HandlerThread("HT");
        thread.start();
        mhandler = new Handler(thread.getLooper());
        mhandler.post(update1);
        thread2=1;
    }
    Runnable update1 = new Runnable() {
        @Override
        public void run() {
            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;
            //DBConnection.linkMysql();

            try {
                conn = DriverManager.getConnection(DBConnection.url, DBConnection.user, DBConnection.pwd);
                System.out.println("连接数据库成功！！！！！！");
                // 对接后使用
                //stmt= conn.prepareStatement("Select * from p_information where user_id='" + mail + "'");
                stmt = conn.prepareStatement("Select * from travel  where user_id='" + mail + "' ");
                rs = stmt.executeQuery();
                Record rc = null;
                while (rs.next()) {
                    rc = new Record();
                    rc.setTime(rs.getString("travel_time"));
                    rc.setLocation(rs.getString("travel_place"));
                    RecordList.add(rc);
                }
                adapter = new RecordAdapter(RecordActivity.this,R.layout.activity_record_adapter,RecordList);
                listView.post(new Runnable() {

                    @Override
                    public void run() {
                        listView.setAdapter(adapter);
                    }
                });
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    };



    protected void onDestroy() {
        if (thread2 > 0) {
            mhandler.removeCallbacks(update1);
        }
        super.onDestroy();
    }
}
