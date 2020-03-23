package com.example.administrator.aninterface;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import dialog.LoadingQuestion;

import com.bumptech.glide.Glide;
import com.daquexian.flexiblerichtextview.FlexibleRichTextView;
import org.scilab.forge.jlatexmath.core.AjLatexMath;

public class Collection extends AppCompatActivity {
    private static final int ShowQuestion = 1;
    private static final int Time_Out = 2;
    private static final int SC_success = 3;
    private static final int SC_cancel = 4;

    private LoadingQuestion load;
    boolean flag = true;//定时器触发标志
    boolean sc = true;//收藏标志

    Timer timer;
    TimerTask task;
    String question_no, question_type, ques, ans, pic_path = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collection);
        ButterKnife.bind(this);
        load = new LoadingQuestion(this);

        //init flexiblerichtextview
        AjLatexMath.init(this);

        Intent intent = getIntent();
        question_no = intent.getStringExtra("question_no");//取得题目编号
        get_question();
    }

    @BindView(R.id.cancel_keep)
    TextView _cancel_keep;
    @BindView(R.id.Question)
    FlexibleRichTextView _Question;
    @BindView(R.id.Answer)
    FlexibleRichTextView _Answer;
    @BindView(R.id.btn_showanswer)
    Button _btn_showanswer;
    @BindView(R.id.que_pic)
    ImageView _que_pic;

    @OnClick({
            R.id.lookup_sc_back,
            R.id.btn_showanswer,
            R.id.cancel_keep,
    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lookup_sc_back://返回
                startActivity(new Intent(this, ErrorQuestion.class));
                finish();
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
                break;
            case R.id.btn_showanswer://显示答案
                show_answer();
                break;
            case R.id.cancel_keep://取消收藏
                Keep();
                break;
        }
    }

    public void Keep() {
        //已收藏时取消收藏
        if (sc) {
            new Thread() {
                public void run() {
                    try {
                        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
                        String username = sharedPreferences.getString("username", "");
                        URL url = new URL("http://129.211.92.156:8000/throw/?question_no=" + question_no + "&username=" + username);
                        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                        urlConn.setDoInput(true);//设置输入流采用字节流
                        urlConn.setDoOutput(true);//设置输出流采用字节流
                        urlConn.setUseCaches(false);//不允许缓存
                        urlConn.setRequestMethod("GET");
                        urlConn.setReadTimeout(5000);
                        urlConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
                        urlConn.setRequestProperty("Charset", "utf_8");
                        urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                        urlConn.setRequestProperty("contentType", "application/json");// 设置文件类型
                        urlConn.setRequestProperty("accept", "application/json");

                        urlConn.connect();//连接服务器

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
                        if (signal.equals("ACK")) {
                            Message message = new Message();
                            message.what = SC_cancel;
                            mHandler.sendMessage(message);
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
        //未收藏时点击收藏
        else {
            new Thread() {
                public void run() {
                    try {
                        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
                        String username = sharedPreferences.getString("username", "");
                        URL url = new URL("http://129.211.92.156:8000/joinerr/?question_no=" + question_no + "&username=" + username);
                        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                        urlConn.setDoInput(true);//设置输入流采用字节流
                        urlConn.setDoOutput(true);//设置输出流采用字节流
                        urlConn.setUseCaches(false);//不允许缓存
                        urlConn.setRequestMethod("GET");
                        urlConn.setReadTimeout(5000);
                        urlConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
                        urlConn.setRequestProperty("Charset", "utf_8");
                        urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                        urlConn.setRequestProperty("contentType", "application/json");// 设置文件类型
                        urlConn.setRequestProperty("accept", "application/json");

                        urlConn.connect();//连接服务器

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
                        if (signal.equals("ACK")) {
                            Message message = new Message();
                            message.what = SC_success;
                            mHandler.sendMessage(message);
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

    public void get_question() {
        load.show();
        flag = true;
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (flag) {
                    load.dismiss();
                    Message message = new Message();
                    message.what = Time_Out;
                    mHandler.sendMessage(message);
                }
            }
        };
        timer.schedule(task, 5000);

        new Thread() {
            public void run() {
                try {
                    URL url = new URL("http://129.211.92.156:8000/wrong/?question_no=" + question_no);
                    HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                    urlConn.setDoInput(true);//设置输入流采用字节流
                    urlConn.setDoOutput(true);//设置输出流采用字节流
                    urlConn.setUseCaches(false);//不允许缓存
                    urlConn.setRequestMethod("GET");
                    urlConn.setReadTimeout(5000);
                    urlConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
                    urlConn.setRequestProperty("Charset", "utf_8");
                    urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    urlConn.setRequestProperty("contentType", "application/json");// 设置文件类型
                    urlConn.setRequestProperty("accept", "application/json");

                    urlConn.connect();//连接服务器

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
                    String signal;
                    try {
                        String tmp = init.toString();
                        JSONObject JSON = new JSONObject(tmp);
                        signal = JSON.getString("RES");
                        //根据服务器返回的信息给出响应
                        if (signal.equals("ACK")) {
                            question_type = JSON.getString("question_type");
                            ques = JSON.getString("que");
                            ans = JSON.getString("ans");
                            pic_path = JSON.getString("pic_path");
                            Message message = new Message();
                            message.what = ShowQuestion;
                            mHandler.sendMessage(message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case (ShowQuestion):
                    if(!pic_path.equals("none"))//需要加载图片
                        Glide.with(Collection.this).load("http://129.211.92.156:8000/media/" + pic_path).error(R.drawable.fail).into(_que_pic);
                    load.dismiss();
                    flag = false;
                    remove_timer();
                    show_question();
                    _btn_showanswer.setVisibility(View.VISIBLE);
                    break;
                case (SC_cancel):
                    cancel_sc();
                    break;
                case (SC_success):
                    sc_success();
                    break;
                case (Time_Out):
                    network_link_timeout();
                    break;
            }
        }
    };

    public void show_question() {
        _Question.setText(ques);
        _Answer.setText("");
    }

    public void show_answer() {
        _Answer.setText(ans);
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

    public void cancel_sc() {
        Drawable topDrawable = getResources().getDrawable(R.drawable.wait);
        topDrawable.setBounds(0, 0, topDrawable.getMinimumWidth(), topDrawable.getMinimumHeight());
        _cancel_keep.setCompoundDrawables(null, topDrawable, null, null);
        sc = false;
        Toast.makeText(this, "已收藏取消", Toast.LENGTH_SHORT).show();
    }

    public void sc_success() {
        Drawable topDrawable = getResources().getDrawable(R.drawable.already);
        topDrawable.setBounds(0, 0, topDrawable.getMinimumWidth(), topDrawable.getMinimumHeight());
        _cancel_keep.setCompoundDrawables(null, topDrawable, null, null);
        sc = true;
        Toast.makeText(this, "已收藏", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, ErrorQuestion.class));
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
