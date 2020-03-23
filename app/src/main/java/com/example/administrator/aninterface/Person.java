package com.example.administrator.aninterface;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class Person extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_center);
        ButterKnife.bind(this);

        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");

        Toolbar toolbar = findViewById(R.id.person_topbar);
        toolbar.setTitle(username + ",Welcome!");//设置个人中心标题
    }

    @OnClick({
            R.id.ButtonExercise,
            R.id.errorquestion,
            R.id.changepsw,
            R.id.off,
    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ButtonExercise:
                startActivity(new Intent(this, Exercise.class));
                finish();
                overridePendingTransition(R.anim.left_in_imediately, R.anim.right_out_imediately);
                break;
            case R.id.errorquestion:
                startActivity(new Intent(this, ErrorQuestion.class));
                finish();
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                break;
            case R.id.changepsw:
                startActivity(new Intent(this, Changepsw.class));
                finish();
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                break;
            case R.id.off://注销
                logout();
                break;
        }
    }

    public void logout() {
        //弹出确认对话框
        AlertDialog.Builder confirm = new AlertDialog.Builder(this);
        confirm.setPositiveButton("确定退出o(´^｀)o", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //清除登录信息
                SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                //跳转登录界面
                startActivity(new Intent(Person.this, LogIn.class));
                finish();
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
            }
        });
        confirm.setNegativeButton("我再想想o(ﾟДﾟ)っ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        confirm.setMessage("真的要退出登录吗?");
        confirm.show();
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
