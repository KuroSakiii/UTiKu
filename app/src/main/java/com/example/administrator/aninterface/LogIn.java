package com.example.administrator.aninterface;

import android.os.Handler;
import android.os.Bundle;
import android.os.Message;
import android.os.StrictMode;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import dialog.LoadingDialog;

public class LogIn extends AppCompatActivity {
    private static final int NAK_1 = 1;
    private static final int NAK_2 = 2;
    private static final int ACK = 3;
    private static final int Time_Out = 4;

    private LoadingDialog dialog;
    boolean flag = true;//定时器触发标志

    Timer timer;
    TimerTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        dialog = new LoadingDialog(this);
    }

    @BindView(R.id.Username)
    EditText _Username;
    @BindView(R.id.Password)
    EditText _Password;

    @OnClick({
            R.id.button_login,
            R.id.TurnToSignUp,
    })
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.button_login://点击登录按钮
                login();
                break;
            case R.id.TurnToSignUp://跳转注册界面
                startActivity(new Intent(this, SignUp.class));
                finish();
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                break;
        }
    }

    public void login() {
        final String username = _Username.getText().toString().trim();
        final String password = _Password.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "请输入用户名!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "请输入密码!", Toast.LENGTH_SHORT).show();
        } else {
            dialog.show();
            //设置定时器
            flag = true;
            timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    if (flag) {
                        dialog.dismiss();
                        Message message = new Message();
                        message.what = Time_Out;
                        mHandler.sendMessage(message);
                    }
                }
            };
            timer.schedule(task, 5000);

            //准备要发送的数据
            final String content = "username=" + username + "&password=" + password;

            //开启子进程进行网络通信
            new Thread() {
                public void run() {
                    try {
                        URL url = new URL("http://129.211.92.156:8000/login/");
                        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                        urlConn.setDoInput(true);//设置输入流采用字节流
                        urlConn.setDoOutput(true);//设置输出流采用字节流
                        urlConn.setUseCaches(false);//不允许缓存
                        urlConn.setRequestMethod("POST");//设置POST方式连接
                        urlConn.setReadTimeout(5000);
                        urlConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
                        urlConn.setRequestProperty("Charset", "utf_8");
                        urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                        urlConn.setRequestProperty("contentType", "application/json");// 设置文件类型
                        urlConn.setRequestProperty("accept", "application/json");

                        urlConn.connect();//连接服务器

                        //发送数据
                        OutputStream out = urlConn.getOutputStream();
                        out.write(content.getBytes("UTF-8"));
                        out.flush();
                        out.close();

                        //接收数据
                        StringBuilder init = new StringBuilder();//初始数据
                        try {
                            int responseCode = urlConn.getResponseCode();
                            if (responseCode == HttpURLConnection.HTTP_OK) {
                                //得到响应流
                                InputStream inputStream = urlConn.getInputStream();
                                //获取响应
                                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    init.append(line);
                                }
                                reader.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        urlConn.disconnect();//断开连接

                        //解析收到的JSON数据
                        String signal = "";
                        try {
                            String tmp = init.toString();
                            JSONObject JSON = new JSONObject(tmp);
                            signal = JSON.getString("RES");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //根据服务器返回的信息给出响应
                        Message message = new Message();
                        if (signal.equals("NAK1")) {
                            message.what = NAK_1;
                        } else if (signal.equals("NAK2")) {
                            message.what = NAK_2;
                        } else if (signal.equals("ACK")) {
                            message.what = ACK;
                        }
                        mHandler.sendMessage(message);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case (NAK_1):
                    dialog.dismiss();
                    flag = false;
                    remove_timer();
                    user_not_exist();
                    break;
                case (NAK_2):
                    dialog.dismiss();
                    flag = false;
                    remove_timer();
                    psw_error();
                    break;
                case (ACK):
                    dialog.dismiss();
                    flag = false;
                    remove_timer();
                    log_success();
                    break;
                case (Time_Out):
                    network_link_timeout();
                    break;
            }
        }
    };

    public void user_not_exist() {
        Toast.makeText(this, "用户名不存在!", Toast.LENGTH_SHORT).show();
        _Username.setText("");//清空输入的用户名
    }

    public void psw_error() {
        Toast.makeText(this, "密码错误!", Toast.LENGTH_SHORT).show();
        _Password.setText("");//清空输入的错误密码
    }

    public void log_success() {
        String username = _Username.getText().toString().trim();
        //保存已登录信息
        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.commit();
        //登录成功,跳转至Exercise界面
        startActivity(new Intent(this, Exercise.class));
        finish();
        Toast.makeText(this, "登录成功!", Toast.LENGTH_SHORT).show();
    }

    public void network_link_timeout() {
        Toast.makeText(this, "网络连接超时!", Toast.LENGTH_SHORT).show();
    }

    public void remove_timer() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (task != null) {
            task.cancel();
            task = null;
        }
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    private long exitTime = 0;

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            System.exit(0);
        }
    }
}
