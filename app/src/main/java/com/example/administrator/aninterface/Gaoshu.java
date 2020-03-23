package com.example.administrator.aninterface;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.scilab.forge.jlatexmath.core.AjLatexMath;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import dialog.LoadingQuestion;
import listclass.Subject;
import adapter.SubjectAdapter;

public class Gaoshu extends AppCompatActivity {
    private static final int Time_Out = 1;
    private static final int Get = 2;

    private LoadingQuestion load;
    boolean flag = true;

    Timer timer;
    TimerTask task;
    String subject_name;
    String chapterlist[];
    List<Subject> ChapterList = new ArrayList<>();//章节列表数组

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gaoshu);
        ButterKnife.bind(this);

        //获得所选择的课程名
        Intent intent = getIntent();
        subject_name = intent.getStringExtra("subject");
        //设置标题为课程名
        _chapter_list_title.setText(subject_name);

        load = new LoadingQuestion(this);
        get_chapter();

        //init flexiblerichtextview
        AjLatexMath.init(this);

        //监听listview点击事件
        _chapter_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Gaoshu.this, Doing.class);
                intent.putExtra("subject", subject_name);//传入课程名
                intent.putExtra("chapter", ChapterList.get(position).getSubject_Name());//传入点击的章节名
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });
    }

    @BindView(R.id.chapter_list_title)
    TextView _chapter_list_title;
    @BindView(R.id.chapter_list)
    ListView _chapter_list;

    @OnClick({
            R.id.chapter_list_back,
    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.chapter_list_back:    //返回
                startActivity(new Intent(this, Exercise.class));
                finish();
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
                break;
        }
    }

    public void get_chapter() {
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
                    URL url = new URL("http://129.211.92.156:8000/show/?course_name=" + subject_name);
                    HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                    urlConn.setDoInput(true);//设置输入流采用字节流
                    urlConn.setDoOutput(true);//设置输出流采用字节流
                    urlConn.setUseCaches(false);//不允许缓存
                    urlConn.setRequestMethod("GET");//设置GET方式连接
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
                            String c = JSON.getString("course_point");
                            chapterlist = c.split(" ");
                            Message message = new Message();
                            message.what = Get;
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
                case (Get):
                    load.dismiss();
                    flag = false;
                    remove_timer();
                    get_c();
                    break;
                case (Time_Out):
                    network_link_timeout();
                    break;
            }
        }
    };

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

    public void get_c() {
        for (int i = 0; i < chapterlist.length; i++) {
            ChapterList.add(new Subject(R.drawable.chapt, chapterlist[i], R.drawable.startexer));
        }
        //设置ListView的数据适配器
        _chapter_list.setAdapter(new SubjectAdapter(this, ChapterList));
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, Exercise.class));
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
