package com.example.pay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ChargeActivity extends AppCompatActivity implements View.OnClickListener{
    private Handler mhandler;
    public EditText money;
    public EditText password;
    public Button confirm;
    public  Button refuse;
    public String mail;
    public String phonenumber;
    public int used =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge);
        Intent i = getIntent();
        mail = i.getStringExtra("userid");
        phonenumber = i.getStringExtra("alipay");
        money = findViewById(R.id.number);
        password = findViewById(R.id.password);
        confirm = (Button) findViewById(R.id.confirm);
        refuse = (Button) findViewById(R.id.refuse);
        refuse.setOnClickListener(this);
    }
    public void consume(View v){
        used=1;
        if(money.length()!=0&&phonenumber!=null) {
            HandlerThread thread = new HandlerThread("HT");
            thread.start();
            mhandler = new Handler(thread.getLooper());
            mhandler.post(buyaccount);
        }
        else
        {
            Toast.makeText(getApplicationContext(), "请绑定支付宝账号", Toast.LENGTH_LONG).show();
        }
    }
    Runnable buyaccount = new Runnable() {
        @Override
        public void run() {
            Connection conn=null;
            PreparedStatement stmt=null;
            ResultSet rs = null;
            //DBConnection.linkMysql();

            try {
                conn = DriverManager.getConnection(DBConnection.url,DBConnection.user,DBConnection.pwd);
                System.out.println("连接数据库成功！！！！！！");
                // 对接后使用，其他activity也是这样
                //stmt= conn.prepareStatement("Select * from p_information where user_id='" + mail + "'");
                double rmoney = Double.parseDouble(money.getText().toString());
                String rpassword = password.getText().toString();
                if(rpassword.equals("123456")) {
                    stmt = conn.prepareStatement("update p_information set balance=balance+'" + rmoney + "'where user_id='" + mail + "'"); //where user_id=
                    int flag = stmt.executeUpdate();
                    if (flag > 0) {
                        Toast.makeText(getApplicationContext(), "充值成功", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ChargeActivity.this,WalletActivity.class);
                        intent.putExtra("userid", mail);
                        startActivity(intent);
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "密码错误", Toast.LENGTH_LONG).show();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }


        }
    };
    protected void onDestroy() {
        if(used>0)
            mhandler.removeCallbacks(buyaccount);
        super.onDestroy();
    }
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.refuse:
                Intent intent = new Intent(this,WalletActivity.class);
                intent.putExtra("userid", mail);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
    }
}
