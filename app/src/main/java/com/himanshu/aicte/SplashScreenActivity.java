package com.himanshu.aicte;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SystemClock.sleep(300);

        Intent intentMain = new Intent(SplashScreenActivity.this, MainActivity.class);

        startActivity(intentMain);
        finish();
    }
}
