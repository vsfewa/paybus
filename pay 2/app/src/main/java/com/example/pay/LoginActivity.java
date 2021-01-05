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

public class LoginActivity extends AppCompatActivity {
    private Handler mhandler;
    public static EditText mail;
    public EditText pwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


    }
    //标记是否调用数据库
    public int used=0;

    //切换至注册
    public void registerClick(View view){
        Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(intent);
    }
    //目前只关联了登录的按钮
    public void onclick(View view){
        used=1;
        //这是读取两个输入框中的内容，R.id.xxx是通过id寻找控件的意思
        mail=(EditText)findViewById(R.id.mailInput);
        pwd=(EditText)findViewById(R.id.pwdInput);

        //通过thread才能调用数据库，在主线程是不能用数据库的
        //需要注意，因为消息返回一定是异步的，所以在做数据库处理的时候尽量不要出现一定要在数据库操作后
        //才能进行的操作，否则可能出现你在访问数据的过程中还为得到结果，程序报错
        HandlerThread thread = new HandlerThread("HT");
        thread.start();
        mhandler = new Handler(thread.getLooper());
        //这里的sqlrunnable就是我们之后要运行的数据库相关的语句，往下面可以看到
        //如果要写新的数据库语句，要再新建新的Runnable对象，并且进行post
        mhandler.post(sqlRunnable);


    }

    //数据库语句，反正你们看就能看懂大概意思我就不解释了，有啥问题再问吧
    Runnable sqlRunnable = new Runnable() {
        @Override
        public void run() {
            Connection conn=null;
            PreparedStatement stmt=null;
            ResultSet rs = null;
            //DBConnection.linkMysql();

            try {
                conn = DriverManager.getConnection(DBConnection.url,DBConnection.user,DBConnection.pwd);
                System.out.println("连接数据库成功！！！！！！");
                stmt= conn.prepareStatement("Select * from account where user_id='"+mail.getText().toString()+"';");
                rs = stmt.executeQuery();
                while (rs.next()) {
                    String pwdReturn=rs.getString("user_password");

                    if(pwdReturn.equals(pwd.getText().toString())){
                        Toast.makeText(getApplicationContext(),"密码正确",Toast.LENGTH_LONG).show();
                        if(rs.getString("rights").equals("administrator")){
                            Intent intent = new Intent(LoginActivity.this, AdminActivity.class);

                            startActivity(intent);
                        }
                        else if(rs.getString("rights").equals("user")){
                            Intent intent = new Intent(LoginActivity.this, UserInfo.class);
                            intent.putExtra("userid", mail.getText().toString());
                            startActivity(intent);

                        }

                        break;
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"密码错误",Toast.LENGTH_LONG).show();

                    }
                };
            } catch (SQLException e) {
                e.printStackTrace();
            }


        }
    };

    @Override
    protected void onDestroy() {
        if(used>0) {
            //销毁线程
            mhandler.removeCallbacks(sqlRunnable);
        }
        super.onDestroy();
    }
}
