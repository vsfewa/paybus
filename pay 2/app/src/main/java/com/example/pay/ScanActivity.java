package com.example.pay;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.tech.Ndef;
import android.nfc.tech.TagTechnology;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Parcelable;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;

public class ScanActivity extends AppCompatActivity {
    //这个是看输出信息的tag，不是测试的话没用
    private static final String TAG = "Mytest";

    //子线程操作
    private Handler mhandler;

    //从数据库读取到的用户信息，对应表p_information
    private String person = "";

    //线程锁，如果数据库线程读取完毕，其变为true
    private boolean SHOW_STATE = false;

    //这个是单模块测试用的user_id,整合的时候是从另一个activity传入的，需要加点代码修改
    private  String user_id = "hzh@123.com";

    private boolean PAY_STATE = false;


    private Intent NFCcard = new Intent();
    private Intent message = new Intent();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "Go into onCreate function" );
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        Intent i = getIntent();
        user_id = i.getStringExtra("userid");

        //读取数据库信息
        HandlerThread thread = new HandlerThread("HT");
        thread.start();
        mhandler = new Handler(thread.getLooper());
        mhandler.post(sqlRunnable);


        //等待数据库线程读取完毕，将数据读取进入person中
        while(true){
            Log.e("account","SHOW_STATE: " + SHOW_STATE);
            if(SHOW_STATE){
                //找到二维码展示的view
                ImageView erweimaView = findViewById(R.id.imageView);
                //根据person信息生成对应二维码
                Bitmap erweima = createQRCodeBitmap(person, 800, 800,"UTF-8","H", "1", Color.GREEN, Color.WHITE);
                //更新界面
                erweimaView.setImageBitmap(erweima);
                Log.e("account",person);
                //SHOW_STATE = false;
                break;
            }
        }
    }

    Runnable sqlRunnable = new Runnable() {
        @Override
        public void run() {
            Connection conn=null;
            PreparedStatement stmt=null;
            ResultSet rs = null;

            try {
                conn = DriverManager.getConnection(DBConnection.url,DBConnection.user,DBConnection.pwd);
                Log.d("account","连接成功");
                stmt= conn.prepareStatement("Select * from p_information where user_id = '" + user_id + "';");
                rs = stmt.executeQuery();
                //这里只读取一条用户信息
                while (rs.next()) {
                    //用户信息用“单个数据” + “#”连接，生成一长串String字符，其中数据用“#”分割，便于二维码生成
                    String info = rs.getString("user_id") + "#" + rs.getString("name") + "#"
                            + rs.getString("idcard") + "#" + rs.getString("phone") + "#"
                            + rs.getString("health_id") + "#" + rs.getString("balance");
                    person = info;
                    Log.e("account","info: " + person);
                };
                //数据库读取成功时，线程锁打开
                SHOW_STATE = true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            //SQL_FLAG = false;
        }
    };

    /**
     *下面的代码是NFC相关内容
     */

    //新加的代码
    //连接“启用NFC”按钮
    //这里的意思是按“启动NFC”按钮后才会打开NFC，然后因为没设备，现在系统设定会自动扣款一元，然后修改个人信息和交易记录
    //如果不这样的话，页面一打开就启动NFC，手机还没靠近过去就扣款了不太现实，总得有个触发的按钮
    public void useNFC(View view) throws IOException, FormatException {
        //编卡id和数据，卡数据直接走数据库抽取，p_information
        NFCcard.putExtra("NFC_id","925336");
        NFCcard.putExtra("NFC_data",person);

        //下面都是自己编的intent,各种命令啥的都是编的，不是真实的
        //模仿来自商家设备的消息
        //命令：1 是商家向买家收款;命令： 2 是自己对别人收款
        message.putExtra("command","1");
        //收款方名称
        message.putExtra("bus_name","202");
        //收款金额
        message.putExtra("amount","1.0");

        //处理交易
        processIntent();
    }


    //  这块的processIntent() 就是处理卡中数据的方法,intent是本机卡数据intent,message是商家设备笑消息
    public void processIntent() throws IOException, FormatException {

        //解析商家数据信息
        String command = message.getStringExtra("command");
        String bus = message.getStringExtra("bus_name");
        double amount = Double.parseDouble(message.getStringExtra("amount"));

        //读取卡信息
        String NFC_id = NfcUtils.readNFCId(NFCcard);
        String data = NfcUtils.readNFCFromTag(NFCcard);
        String[] arr_data = data.split("#");
        double balance = Double.parseDouble(arr_data[5]);
        Log.e("account","balance##################################: " + balance);
        Log.e("account","before_person: " + person);

        //处理消息命令
        if(command == "1"){
            //自己向别人付款
            if(balance >= 1.0){
                balance -= amount;
                PAY_STATE = true;
            }
            else{
                Toast.makeText(getApplicationContext(),"余额不足，请前往充值",Toast.LENGTH_LONG).show();
                PAY_STATE = false;
            }
        }
        else if(command == "2"){
            balance += amount;
            PAY_STATE = true;
        }
        else;

        if(PAY_STATE){
            //回写进入NFC卡
            arr_data[5] = String.valueOf(balance);
            String after_data = "";
            for(int i = 0;i < arr_data.length;i++){
                if(i != 0)after_data += "#";
                after_data += arr_data[i];
            }
            person = after_data;
            NfcUtils.writeNFCToTag(after_data,NFCcard);
            Log.e("account","after_person: " + person);

            //修改数据库p_information
            HandlerThread thread = new HandlerThread("HT");
            thread.start();
            mhandler = new Handler(thread.getLooper());
            mhandler.post(NFCUpdate);
        }
    }

    Runnable NFCUpdate = new Runnable() {
        @Override
        public void run() {
            //NFC_FLAG = true;

            Connection conn=null;
            PreparedStatement stmt=null;
            ResultSet rs = null;

            try {
                //连接数据库
                conn = DriverManager.getConnection(DBConnection.url,DBConnection.user,DBConnection.pwd);
                Log.e("account","连接成功");
                //分割数据，arr[5]对应着账户余额
                String[] arr = person.split("#");
                Log.e("account","balance: " + arr[5]);

                //更新p_information表
                String sql = "UPDATE p_information SET balance = ? WHERE user_id = ?;";
                stmt= conn.prepareStatement(sql);
                stmt.setString(1,arr[5]);
                stmt.setString(2,user_id);
                Log.e("account", "update p_information: " + stmt);
                int update_rs = stmt.executeUpdate();
                Log.e("account", "update p_information: 成功 " + update_rs);

                //获取当前时间，并按照2020-06-30 15:40:33时间模板记录
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String strDate = sdf.format(c.getTime());
                Log.e("account",strDate);

                //插入consumption表(消费记录表)
                sql = "INSERT INTO consumption (user_id,consumption_time,consumption_place,consumption_amount,consumption_object) " +
                        "VALUES (?,?,?,?,?)";
                stmt= conn.prepareStatement(sql);
                stmt.setString(1,user_id);
                stmt.setString(2,strDate);
                stmt.setString(3,"浙江大学玉泉校区");
                stmt.setString(4,message.getStringExtra("amount"));
                stmt.setString(5,message.getStringExtra("bus_name"));
                Log.e("account", "Insert into consumption: " + stmt);
                int insert_rs = stmt.executeUpdate();
                Log.e("account", "Insert into consumption: 成功 " + insert_rs);

                //插入travel表
                sql = "INSERT INTO travel (user_id,travel_time,travel_place,health_state) " +
                        "VALUES (?,?,?,?)";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1,user_id);
                stmt.setString(2,strDate);
                stmt.setString(3,"浙江大学玉泉校区");
                stmt.setString(4,"健康");
                Log.e("account", "Insert into travel: " + stmt);
                int travel_rs = stmt.executeUpdate();
                Log.e("account", "Insert into travel: 成功 " + travel_rs);

                if(update_rs > 0 && insert_rs > 0 && travel_rs > 0)
                    Toast.makeText(getApplicationContext(),"刷卡成功",Toast.LENGTH_LONG).show();

            } catch (SQLException e) {
                e.printStackTrace();
            }
           // NFC_FLAG = false;
        }
    };

    @Override
    protected void onDestroy() {
        Log.e(TAG, "Go into onPause function" );
        super.onDestroy();
        //NfcUtils.mNfcAdapter = null;
//        if(!SQL_FLAG)mhandler.removeCallbacks(sqlRunnable);
//        if(!NFC_FLAG)mhandler.removeCallbacks(NFCUpdate);
        Log.e("account","destory");
    }

    /**
     * 下面的代码是二维码生成的代码
     * 生成简单二维码
     *
     * @param content                字符串内容
     * @param width                  二维码宽度
     * @param height                 二维码高度
     * @param character_set          编码方式（一般使用UTF-8）
     * @param error_correction_level 容错率 L：7% M：15% Q：25% H：35%
     * @param margin                 空白边距（二维码与边框的空白区域）
     * @param color_black            黑色色块
     * @param color_white            白色色块
     * @return BitMap
     */
    public static Bitmap createQRCodeBitmap(String content, int width, int height,
                                            String character_set, String error_correction_level,
                                            String margin, int color_black, int color_white) {
        // 字符串内容判空
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        // 宽和高>=0
        if (width < 0 || height < 0) {
            return null;
        }
        try {
            /** 1.设置二维码相关配置 */
            Hashtable<EncodeHintType, String> hints = new Hashtable<>();
            // 字符转码格式设置
            if (!TextUtils.isEmpty(character_set)) {
                hints.put(EncodeHintType.CHARACTER_SET, character_set);
            }
            // 容错率设置
            if (!TextUtils.isEmpty(error_correction_level)) {
                hints.put(EncodeHintType.ERROR_CORRECTION, error_correction_level);
            }
            // 空白边距设置
            if (!TextUtils.isEmpty(margin)) {
                hints.put(EncodeHintType.MARGIN, margin);
            }
            /** 2.将配置参数传入到QRCodeWriter的encode方法生成BitMatrix(位矩阵)对象 */
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);

            /** 3.创建像素数组,并根据BitMatrix(位矩阵)对象为数组元素赋颜色值 */
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    //bitMatrix.get(x,y)方法返回true是黑色色块，false是白色色块
                    if (bitMatrix.get(x, y)) {
                        pixels[y * width + x] = color_black;//黑色色块像素设置
                    } else {
                        pixels[y * width + x] = color_white;// 白色色块像素设置
                    }
                }
            }
            /** 4.创建Bitmap对象,根据像素数组设置Bitmap每个像素点的颜色值,并返回Bitmap对象 */
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }


    /***
     * 下面的代码是二维码扫码的代码
     */

    public void onClick(View view) {
        //android6.0以上需要动态申请相机等权限，
        getPrimission();
        //启动扫码，CaptureActivity是第三方库
        Intent intent = new Intent(ScanActivity.this, CaptureActivity.class);
        startActivityForResult(intent, 0);//REQUEST_CODE_SCAN=0
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 扫描二维码/条码回传
        if (requestCode == 0 && resultCode == RESULT_OK) {//RESULT_OK=-1
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);

                //当扫码结果出来时，打开另一个activity，并将扫码的结果传递至另一个activity：SaomaActivity
                Intent intent = new Intent(ScanActivity.this, SaomaResult.class);
                //将数据放到intent中传递
                intent.putExtra("info",content);
                startActivity(intent);
            }
        }
    }
    //动态申请相机权限
    private void getPrimission() {
        PackageManager pm = getPackageManager();
        boolean permission = (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission("android.permission.CAMERA", "com.zhengyuan.learningqrscan"));
        if (permission) {
            //"有这个权限"
            Toast.makeText(ScanActivity.this, "有权限", Toast.LENGTH_SHORT).show();
        } else {
            //"没有这个权限"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 15);
            }
        }
    }

}
