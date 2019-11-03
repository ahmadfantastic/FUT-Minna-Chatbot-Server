package com.fut.chatbot.util;

import com.africastalking.AfricasTalking;
import com.africastalking.SmsService;
import com.africastalking.sms.Recipient;
import java.io.IOException;
import java.util.List;

public class SMS {

    public static boolean sendTo(String number, String message) {
        String USERNAME = "sandbox";
        String API_KEY = "a8ee42a9887deb9a4dc0c5b815987091e6435a7a4e45a38be734d23df0a3dfa5";

        AfricasTalking.initialize(USERNAME, API_KEY);
        SmsService sms = AfricasTalking.getService(AfricasTalking.SERVICE_SMS);
        try {
            number = "+234" + number.substring(1);//change to international standard
            List<Recipient> response = sms.send(message, "FUT Chatbot", new String[]{number}, false);
            if (!response.isEmpty()) {
                System.out.print(response.get(0).number);
                System.out.print(" : ");
                System.out.println(response.get(0).status);

                return response.get(0).status.toLowerCase().contains("success");
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
