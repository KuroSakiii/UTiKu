package com.example.administrator.aninterface;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import dialog.LoadingQuestion;
import listclass.Subject;
import adapter.SubjectAdapter;

public class Exercise extends AppCompatActivity {
    private static final int Time_Out = 1;
    private static final int Get = 2;

    private LoadingQuestion load;
    boolean flag = true;

    Timer timer;
    TimerTask task;
    String subject[];
    List<Subject> SubjectList = new ArrayList<>();//课程列表数组

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercise);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.exercise_topbar);
        toolbar.setTitle("课程列表");//设置标题

        load = new LoadingQuestion(this);
        get_subject();

        //监听listview点击事件
        _subject_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Exercise.this, Gaoshu.class);
                intent.putExtra("subject", SubjectList.get(position).getSubject_Name());//传入点击的课程名
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });
    }

    @BindView(R.id.subject_list)
    ListView _subject_list;

    @OnClick({
            R.id.ButtonPerson,
    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ButtonPerson://点击导航栏个人中心
                startActivity(new Intent(this, Person.class));
                finish();
                overridePendingTransition(R.anim.right_in_imediately, R.anim.left_out_imediately);
                break;
        }
    }

    public void get_subject() {
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
                    URL url = new URL("http://129.211.92.156:8000/getsubject/");
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
                            String s = JSON.getString("subject");
                            subject = s.split(" ");
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
                    get_s();
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

    public void get_s() {
        for (int i = 0; i < subject.length; i++) {
            SubjectList.add(new Subject(R.drawable.lessons, subject[i], R.drawable.gostraight));
        }
        //设置ListView的数据适配器
        _subject_list.setAdapter(new SubjectAdapter(this, SubjectList));
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
