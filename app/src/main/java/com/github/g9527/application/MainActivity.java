package com.github.g9527.application;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

import com.github.g9527.application.views.BillActivity;
import com.github.g9527.application.views.ControlActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        View controlView = findViewById(R.id.main_icon_1);
        controlView.setOnClickListener(this);
        View billView = findViewById(R.id.main_icon_2);
        billView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Class<?> cl = null;
        switch (v.getId()) {
            case R.id.main_icon_1:
                cl = ControlActivity.class;
                break;
            case R.id.main_icon_2:
                cl = BillActivity.class;
                break;
        }

        Intent intent = new Intent(this, cl);
        startActivity(intent);
    }
}