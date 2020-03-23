package com.example.administrator.aninterface;

import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import dialog.DataSubmit;

public class Changepsw extends AppCompatActivity {
    private static final int NAK = 1;
    private static final int ACK = 2;
    private static final int Time_Out = 3;

    private DataSubmit submit;
    boolean flag = true;

    Timer timer;
    TimerTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changepassword);
        ButterKnife.bind(this);
        submit = new DataSubmit(this);
    }

    @BindView(R.id.oldpassword)
    EditText _oldpassword;
    @BindView(R.id.newpassword)
    EditText _newpassword;
    @BindView(R.id.confirmpassword)
    EditText _confirmpassword;

    @OnClick({
            R.id.activity_changepsw_back,
            R.id.btn_changepsw,
    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_changepsw_back:    //返回个人中心
                startActivity(new Intent(this, Person.class));
                finish();
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
                break;
            case R.id.btn_changepsw:    //按下修改密码按钮
                changepassword();
                break;
        }
    }

    public void changepassword() {
        String oldpsw = _oldpassword.getText().toString().trim();
        String newpsw = _newpassword.getText().toString().trim();
        String comfirmpsw = _confirmpassword.getText().toString().trim();
        if (TextUtils.isEmpty(oldpsw))
            Toast.makeText(this, "请输入原密码!", Toast.LENGTH_SHORT).show();
        else if (TextUtils.isEmpty(newpsw)) {
            Toast.makeText(this, "请输入新密码!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(comfirmpsw)) {
            Toast.makeText(this, "请再次输入新密码!", Toast.LENGTH_SHORT).show();
        } else if (!newpsw.equals(comfirmpsw)) {
            Toast.makeText(this, "两次输入的密码不一致!", Toast.LENGTH_SHORT).show();
        } else {
            submit.show();
            flag = true;
            timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    if (flag) {
                        submit.dismiss();
                        Message message = new Message();
                        message.what = Time_Out;
                        mHandler.sendMessage(message);
                    }
                }
            };
            timer.schedule(task, 5000);

            SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
            String username = sharedPreferences.getString("username", "");
            final String send = "username=" + username + "&oldpassword=" + oldpsw + "&newpassword=" + newpsw;
            new Thread() {
                public void run() {
                    try {
                        URL url = new URL("http://129.211.92.156:8000/changepsw/");
                        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                        urlConn.setDoInput(true);//设置输入流采用字节流
                        urlConn.setDoOutput(true);//设置输出流采用字节流
                        urlConn.setUseCaches(false);//不允许缓存
                        urlConn.setRequestMethod("POST");
                        urlConn.setReadTimeout(5000);
                        urlConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
                        urlConn.setRequestProperty("Charset", "utf_8");
                        urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                        urlConn.setRequestProperty("contentType", "application/json");// 设置文件类型
                        urlConn.setRequestProperty("accept", "application/json");

                        urlConn.connect();//连接服务器

                        //发送数据
                        OutputStream out = urlConn.getOutputStream();
                        out.write(send.getBytes("UTF-8"));
                        out.flush();
                        out.close();

                        //接收数据
                        StringBuilder init = new StringBuilder();
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
                            message.what = NAK;
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
                case (NAK):
                    submit.dismiss();
                    flag = false;
                    remove_timer();
                    old_password_error();
                    break;
                case (ACK):
                    submit.dismiss();
                    flag = false;
                    remove_timer();
                    changepassword_success();
                    break;
                case (Time_Out):
                    network_link_timeout();
                    break;
            }
        }
    };

    public void old_password_error() {
        Toast.makeText(this, "原密码错误!", Toast.LENGTH_SHORT).show();
    }

    public void changepassword_success() {
        //密码修改成功,返回个人中心界面
        startActivity(new Intent(this, Person.class));
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
        Toast.makeText(this, "密码修改成功!", Toast.LENGTH_SHORT).show();
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
    public void onBackPressed() {
        startActivity(new Intent(this, Person.class));
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
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
}
