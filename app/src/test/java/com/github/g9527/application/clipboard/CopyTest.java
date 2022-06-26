package com.github.g9527.application.clipboard;

import com.github.g9527.application.core.clipboard.FileData;
import com.github.g9527.application.core.clipboard.TextData;
import com.google.gson.Gson;

import org.junit.Test;

import java.util.Map;

public class CopyTest {

    @Test
    public void json() {
        Gson gson = new Gson();
        String json = "{'type':'text'}";
        Map map = gson.fromJson(json, Map.class);
        if (map.get("type").equals("text")) {
            TextData textData = gson.fromJson(json, TextData.class);
            System.out.println(textData.getData());
        }

        json = "{'type':'file'}";
        if (map.get("type").equals("file")) {
            FileData textData = gson.fromJson(json, FileData.class);
            System.out.println(textData.getData());
        }
    }
}
