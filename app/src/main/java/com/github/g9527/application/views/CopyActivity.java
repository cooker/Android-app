package com.github.g9527.application.views;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.g9527.application.R;
import com.github.g9527.application.core.Constants;
import com.github.g9527.application.core.clipboard.FileData;
import com.github.g9527.application.core.clipboard.TextData;
import com.github.g9527.application.core.utils.OkHttpUtils;
import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
                    Response jsonObject = (Response) msg.obj;
                    try {
                        doCopyHandler(jsonObject.body().byteString().string(Charset.defaultCharset()));
                    } catch (IOException e) {
                        Log.e(TAG, "数据解析失败", e);
                    }
                    break;
                case 2:
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
                        Message obtain = Message.obtain();
                        String host = editText.getText().toString();
                        obtain.what = 1;

                        Request r = OkHttpUtils.getInstance().requestBuilder().url("http://" + host + "/")
                                .header(Constants.X_API_VERSION, "1")
                                .build();
                        Response response = null;
                        try {
                            response =  OkHttpUtils.getInstance().newCall(r).execute();
                        } catch (IOException e) {
                            Log.e(TAG, "请求异常", e);
                            return;
                        }
                        obtain.obj = response;
                        String body = response.body().toString();
                        if (200 == response.code() && !StringUtils.isEmpty(body)) {

                        } else {
                            return;
                        }
                        handler.sendMessage(obtain);
                    }
                }.start();
                break;
            case R.id.copy_btn_cut:
                break;
        }
    }

    private void doCopyHandler(String jsonObject){
        Gson gson = new Gson();
        Map map = gson.fromJson(jsonObject, Map.class);
        String type = (String) map.get("type");
        ClipData clipData = null;
        if ("text".equals(type)) {
            TextData textData = gson.fromJson(jsonObject, TextData.class);
            clipData = ClipData.newPlainText(null, textData.getData());
        } else if ("file".equals(type)) {
            FileData fileData = gson.fromJson(jsonObject, FileData.class);
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
    }
}