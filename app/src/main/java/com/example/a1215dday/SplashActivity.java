package com.example.a1215dday;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//         3초 동안 스플래시 화면을 보여주고 메인 화면으로 이동
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                pageToMain();  // 3초 후에 메인 화면으로 이동
            }
        }, 3000); // 3000ms = 3초

    }
    // 페이지 이동
    private void pageToMain(){
        Intent intent = new Intent(SplashActivity.this,LoginActivity.class); //LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
