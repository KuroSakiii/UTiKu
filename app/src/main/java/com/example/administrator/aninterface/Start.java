package com.example.administrator.aninterface;

import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

public class Start extends AppCompatActivity {
    private static final int GOTO_LOGIN = 1;
    private static final int GOTO_EXERCISE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = LayoutInflater.from(this).inflate(R.layout.activity_start, null);
        setContentView(view);

        AlphaAnimation anime = new AlphaAnimation(0.7f, 1.0f);
        anime.setDuration(2000);
        view.startAnimation(anime);
        anime.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                //检查是否已有登录信息
                SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
                String username = sharedPreferences.getString("username", "nouser");
                if (username.equals("nouser")) {
                    mHandler.sendEmptyMessageDelayed(GOTO_LOGIN, 500);
                } else {
                    mHandler.sendEmptyMessageDelayed(GOTO_EXERCISE, 500);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
        });
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GOTO_EXERCISE: {
                    startActivity(new Intent(Start.this, Exercise.class));
                    finish();
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                }
                break;
                case GOTO_LOGIN: {
                    startActivity(new Intent(Start.this, LogIn.class));
                    finish();
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                }
                break;
            }
        }
    };
}
