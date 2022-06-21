package com.github.g9527.application.views;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.github.g9527.application.R;

public class CopyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copy);
        setTitle("剪切板");
    }
}