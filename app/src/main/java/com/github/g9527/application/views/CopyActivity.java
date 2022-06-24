package com.github.g9527.application.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.github.g9527.application.R;

public class CopyActivity extends AppCompatActivity {

    private EditText editText;
    SharedPreferences conf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copy);
        setTitle("剪切板");
        editText = findViewById(R.id.copy_edit);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (EditorInfo.IME_ACTION_DONE == actionId || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    SharedPreferences.Editor edit = conf.edit();
                    edit.putString("serverHost", v.getText().toString());
                    edit.commit();
                    editText.clearFocus();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        conf = getSharedPreferences("copy", Context.MODE_PRIVATE);
        String serverHost = conf.getString("serverHost", null);
        editText.setText(serverHost);
    }
}