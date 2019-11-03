/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fut.chatbot.controlller;

import com.fut.chatbot.model.User;
import com.fut.chatbot.repo.UserRepo;
import com.fut.chatbot.util.Constants;
import com.fut.chatbot.util.Generator;
import com.fut.chatbot.util.PatternChecker;
import com.fut.chatbot.util.Result;
import com.fut.chatbot.util.SMS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author ahmad
 */
@RestController
@RequestMapping("rest")
public class RESTController {

    @Autowired
    private UserRepo userRepo;

    @PostMapping("activate-phone")
    public Result activatePhone(@RequestHeader("key") String key, @RequestParam("phone") String phone) {
        Result response;
        if (!key.equals(Constants.KEY)) {
            response = new Result(Result.Status.INVALID_KEY, "Invalid Application Key");
        } else if (!PatternChecker.checkPhone(phone)) {
            response = new Result(Result.Status.ERROR, "Invalid Phone Number");
        } else {
            String code = Generator.generateNumberString(5);
            if (SMS.sendTo(phone, "FUT Chatbot Code: " + code)) {
                User user = userRepo.findByPhone(phone);
                if (user == null) {
                    user = new User();
                    user.setPhone(phone);
                    user = userRepo.save(user);
                }
                user.setCode(code);
                response = new Result(Result.Status.SUCCESS, user);
            } else {
                response = new Result(Result.Status.ERROR, "Unable to send SMS to this phone number");
            }
        }
        return response;
    }

    @PostMapping("activate-code")
    public Result activateCode(@RequestHeader("key") String key, @RequestParam("phone") String phone, @RequestParam("code") String code) {
        Result response;
        if (!key.equals(Constants.KEY)) {
            response = new Result(Result.Status.INVALID_KEY, "Invalid Application Key");
        } else if (!userRepo.existsByPhone(phone)) {
            response = new Result(Result.Status.INVALID_USER, "Invalid User");
        } else {
            User user = userRepo.findByPhone(phone);
            if (PatternChecker.checkCode(code)) {
                user.setCode(code);
                user = userRepo.save(user);
                response = new Result(Result.Status.SUCCESS, user);
            } else {
                response = new Result(Result.Status.INVALID_CODE, "Invalid code provided");
            }
        }
        return response;
    }

    @PostMapping("update-name")
    public Result updateName(@RequestHeader("key") String key, @RequestParam("phone") String phone, @RequestParam("code") String code, @RequestParam("newName") String newName) {
        Result response;
        if (!key.equals(Constants.KEY)) {
            response = new Result(Result.Status.INVALID_KEY, "Invalid Application Key");
        } else if (!userRepo.existsByPhone(phone)) {
            response = new Result(Result.Status.INVALID_USER, "Invalid User");
        } else {
            User user = userRepo.findByPhone(phone);
            if (user.getCode().equals(code)) {
                user.setName(newName);
                user = userRepo.save(user);
                response = new Result(Result.Status.SUCCESS, user);
            } else {
                response = new Result(Result.Status.INVALID_CODE, "Wrong code provided");
            }
        }
        return response;
    }

    @PostMapping("update-phone")
    public Result updatePhone(@RequestHeader("key") String key, @RequestParam("phone") String phone, @RequestParam("code") String code, @RequestParam("newPhone") String newPhone) {
        Result response;
        if (!key.equals(Constants.KEY)) {
            response = new Result(Result.Status.INVALID_KEY, "Invalid Application Key");
        } else if (!PatternChecker.checkPhone(phone)) {
            response = new Result(Result.Status.ERROR, "Invalid Phone Number");
        } else if (!userRepo.existsByPhone(phone)) {
            response = new Result(Result.Status.INVALID_USER, "Invalid User");
        } else {
            User user = userRepo.findByPhone(phone);
            if (user.getCode().equals(code)) {
                String newCode = Generator.generateNumberString(5);
                if (SMS.sendTo(phone, "FUT Chatbot Code: " + newCode)) {
                    user.setPhone(newPhone);
                    user.setCode(newCode);
                    user = userRepo.save(user);
                    response = new Result(Result.Status.SUCCESS, user);
                } else {
                    response = new Result(Result.Status.ERROR, "Unable to send SMS to the new phone number");
                }
            } else {
                response = new Result(Result.Status.INVALID_CODE, "Wrong code provided");
            }
        }
        return response;
    }
}
