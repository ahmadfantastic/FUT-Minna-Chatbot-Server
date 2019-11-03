/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fut.chatbot.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author ahmad
 */
public class PatternChecker {
    
    public static boolean checkPhone(String phone){
        return check("\\d{11}", phone);
    }
    
    public static boolean checkCode(String code){
        return check("\\d{5}", code);
    }
    
    public static boolean checkEmail(String email){
        return check("^[A-Za-z0-9-.]+(\\-[A-Za-z0-9])*@[A-Za-z0-9-]+(\\.[A-Za-z0-9])", email);
    }
    
    public static boolean check(String pattern, String data){
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(data);
        return m.find();
    }
}
