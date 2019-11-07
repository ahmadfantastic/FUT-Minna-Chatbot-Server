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
    
    public static final int BROKER_SCHEDULER_HOUR_INTERVAL = 1;
    
    public static final double QUESTION_MINIMUM_SCORE = 0.7;
    public static final double QUESTION_SCORE_DIFFERENCE = 0.3;
    public static final double QUESTION_COUNT_SCORE = 0.05;
    public static final double QUESTION_RIGHT_SCORE = 0.1;
    public static final double QUESTION_WRONG_SCORE = -0.1;
    public static final double QUESTION_TAG_SCORE = 0.5;
    
    public static final int MESSAGE_EXPIRY_DAYS = 7;
    
    public static final String MESSAGE_QUESTION_UNKNOWN = "I do not understand what you said,"
            + " please be specific. Remember i am just a robot and know only information relating FUT Minna";
    public static final String MESSAGE_QUESTION_NO_ANSWER = "Unfurnately i dont know the answer to your question."
            + " Ask me again later !";
    public static final String MESSAGE_QUESTION_EXPIRED_ANSWER = "Last time i know\n";
    
    public static final int TAG_SUBCRIBTION_ASK_DAYS = 7;
    public static final int TAG_SUBCRIPTION_ASK_COUNT = 10;
    
}
