package com.fut.chatbot.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Constants {
    public static final boolean IS_ONLINE = false;
    public static final Gson GSON = new Gson();
    public static final Gson GSON_EXPOSE = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    public static final String IMAGE_BASE_PATH = IS_ONLINE ? System.getProperty("java.io.tmpdir") + System.getProperty("file.separator"): "C:/xampp/htdocs/chatbot/";
    public static final String IMAGE_LOCAL_BASE = IS_ONLINE ? "":"http://192.168.43.149/chatbot/";
    public static final String DEFAULT_PASSWORD = "12345";
    
    public static final String KEY = "QWERTYUIOP";
}
