package com.example.a1215dday;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.ComponentActivity;

public class LoginActivity extends ComponentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button btn1 = findViewById(R.id.login_btn);

        View.OnClickListener listener = new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {
                if(v.getId() == R.id.login_btn){
                    //로그인 버튼이 눌렸을 때
                    //1. 아이디 정규식 체크
                    //2. 비밀번호 정규식 체크
                    //3. DB로 접속 후 체크


                    //4. BlueTooth Activity로 이동
                    Intent intent = new Intent(LoginActivity.this,BluetoothActivity.class); //LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else if (v.getId() == R.id.kakaobutton) {
                    //카카오 로그인 알고리즘
                } else {
                    Log.d("ERR1", "없는 버튼인데?");
                }
            }
        };
        // btn1에 OnClickListener 설정
        btn1.setOnClickListener(listener);
    }


}