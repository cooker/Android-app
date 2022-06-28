package com.github.g9527.application.views;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.g9527.application.R;
import com.github.g9527.application.core.Constants;
import com.github.g9527.application.core.api.Api;
import com.github.g9527.application.core.api.ApiCallback;
import com.github.g9527.application.core.clipboard.FileData;
import com.github.g9527.application.core.clipboard.TextData;
import com.github.g9527.application.core.utils.GsonUtils;
import com.github.g9527.application.core.utils.OkHttpUtils;
import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import kotlin.collections.MapsKt;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 需要下载，https://github.com/YanxinTang/clipboard-online
 */
public class CopyActivity extends AppCompatActivity implements TextView.OnEditorActionListener, View.OnClickListener {

    public static final String TAG = CopyActivity.class.getClass().getSimpleName();

    private EditText editText;
    SharedPreferences conf;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    String json = msg.getData().getString("body");
                    try {
                        doCopyHandler(json);
                    } catch (Exception e) {
                        Log.e(TAG, "copy 操作失败", e);
                        Toast.makeText(CopyActivity.this, "操作失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 2:
                    String text = msg.getData().getString("text");
                    TextData textData = new TextData(text);
                    String host = editText.getText().toString();
                    Map<String, String> hmap = new HashMap<>();
                    hmap.put(Constants.X_API_VERSION, "1");
                    Api api = Api.config("http://" + host + "/", null, hmap, textData);
                    api.post(new ApiCallback() {
                        @Override
                        public void onSuccess(Response response) {
                            Looper.prepare();
                            Toast.makeText(CopyActivity.this, "黏贴成功", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Looper.prepare();
                            Toast.makeText(CopyActivity.this, "黏贴失败", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    });
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copy);
        setTitle("剪切板");
        editText = findViewById(R.id.copy_edit);
        editText.setOnEditorActionListener(this);
        findViewById(R.id.copy_btn_copy).setOnClickListener(this);
        findViewById(R.id.copy_btn_cut).setOnClickListener(this);
        if(Build.VERSION.SDK_INT>=24){
            try{
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        conf = getSharedPreferences("copy", Context.MODE_PRIVATE);
        String serverHost = conf.getString("serverHost", null);
        editText.setText(serverHost);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (v.getId() == R.id.copy_edit) {
            if (EditorInfo.IME_ACTION_DONE == actionId || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                SharedPreferences.Editor edit = conf.edit();
                if (StringUtils.isEmpty(v.getText().toString())) {
                    v.setText(v.getHint());
                }
                edit.putString("serverHost", v.getText().toString());
                edit.commit();
                editText.clearFocus();
                return true;
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.copy_btn_copy:
                new Thread() {
                    @Override
                    public void run() {

                        String host = editText.getText().toString();
                        Map<String, String> hmap = new HashMap<>();
                        hmap.put(Constants.X_API_VERSION, "1");
                        Api api = Api.config("http://" + host + "/", null, hmap, null);
                        api.get(new ApiCallback() {
                            @Override
                            public void onSuccess(Response response) {
                                Message obtain = Message.obtain();
                                obtain.what = 1;
                                Bundle bundle = new Bundle();
                                String body = null;
                                try {
                                    body = response.body().string();
                                } catch (IOException e) {
                                    Looper.prepare();
                                    Toast.makeText(CopyActivity.this, "响应失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                    return;
                                }
                                bundle.putString("body", body);
                                obtain.setData(bundle);
                                if (200 == response.code() && !StringUtils.isEmpty(body)) {
                                    handler.sendMessage(obtain);
                                } else {
                                    Looper.prepare();
                                    Toast.makeText(CopyActivity.this, "请求失败：" + response.code(), Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Looper.prepare();
                                Toast.makeText(CopyActivity.this, "请求失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                Looper.loop();

                            }
                        });
                    }
                }.start();
                break;
            case R.id.copy_btn_cut:
                doCutHandler();
                break;
        }
    }

    private void doCutHandler() {
        ClipboardManager clipboardManager = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = clipboardManager.getPrimaryClip();
        if (clipData != null && clipData.getItemCount() > 0) {
            String text = clipData.getItemAt(0).getText().toString();
            Message obtain = Message.obtain();
            obtain.what = 1;
            Bundle bundle = new Bundle();
            bundle.putString("text", text);
            obtain.setData(bundle);
            handler.sendMessage(obtain);
        }
    }

    private void doCopyHandler(String jsonObject){
        Map map = GsonUtils.toBean(jsonObject, Map.class);
        String type = (String) map.get("type");
        ClipData clipData = null;
        if ("text".equals(type)) {
            TextData textData = GsonUtils.toBean(jsonObject, TextData.class);
            clipData = ClipData.newPlainText(null, textData.getData());
        } else if ("file".equals(type)) {
            FileData fileData = GsonUtils.toBean(jsonObject, FileData.class);
            File cacheDir = getCacheDir();
            List<File> fileList = new ArrayList<>();
            for (FileData.DataDTO it : fileData.getData()) {
                try {
                    File tempFile = File.createTempFile(UUID.randomUUID().toString(),
                            "."+StringUtils.substringAfter(it.getName(), "."), cacheDir);
                    byte[] decode = Base64.decode(it.getContent(), Base64.DEFAULT);
                    FileOutputStream fos = new FileOutputStream(tempFile);
                    fos.write(decode);
                    fos.close();
                    fileList.add(tempFile);
                    clipData = ClipData.newRawUri(null, Uri.fromFile(tempFile));
                    break;
                } catch (IOException e) {
                    Log.e(TAG, "创建零时文件失败", e);
                }
            }
        }

        ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(clipData);
        Toast.makeText(CopyActivity.this, "操作成功", Toast.LENGTH_SHORT).show();
    }
}