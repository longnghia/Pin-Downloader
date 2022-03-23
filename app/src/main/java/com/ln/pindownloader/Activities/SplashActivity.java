package com.ln.pindownloader.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.ln.pindownloader.R;
import com.ln.pindownloader.Utils.DefaultUtils;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.splash_layout);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}