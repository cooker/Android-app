package com.github.g9527.application;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.g9527.application.apdater.AppViewApdater;
import com.github.g9527.application.core.AppViewEntity;
import com.github.g9527.application.views.BillActivity;
import com.github.g9527.application.views.ControlActivity;
import com.github.g9527.application.views.CopyActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static String TAG = MainActivity.class.getSimpleName();

    List<AppViewEntity> items = null;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        GridView gridView = findViewById(R.id.main_layout);
        items = new ArrayList<>();
        items.add(new AppViewEntity(1L, R.mipmap.baixiangguo, "遥控器"));
        items.add(new AppViewEntity(2L, R.mipmap.juzi, "记账本"));
        items.add(new AppViewEntity(3L, R.mipmap.shanzhu, "剪切板"));
        AppViewApdater apdater = new AppViewApdater(this, items);
        gridView.setAdapter(apdater);
        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG, "点击了 > " + items.get(position).getLabel());
        Class jumpCl = null;
        if (id == 1L) {
            jumpCl = ControlActivity.class;
        } else if (id == 2L){
            jumpCl = BillActivity.class;
        } else if (id == 3L){
            jumpCl = CopyActivity.class;
        }
        Intent intent = new Intent(this, jumpCl);
        startActivity(intent);
    }
}