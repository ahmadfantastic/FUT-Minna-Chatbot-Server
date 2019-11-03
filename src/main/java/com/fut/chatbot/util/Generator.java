package com.fut.chatbot.util;

public class Generator {

    public static String generateNumberString(int count) {
        String text = "";
        for (int i = 0; i < count; i++) {
            text += (int) (Math.random() * 10);
        }
        return text;
    }
}
