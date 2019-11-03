/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fut.chatbot.controlller;

import com.fut.chatbot.model.*;
import com.fut.chatbot.repo.*;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author ahmad
 */
@Controller
public class PublicController {

    @Autowired
    ContributorRepo contributorRepo;

    @RequestMapping("")
    public String index(HttpSession session, Model model) {
        return "public/login";
    }

    @GetMapping("login")
    public String gotoLogin(HttpSession session, Model model) {
        model.addAttribute("success", session.getAttribute("success"));
        model.addAttribute("message", session.getAttribute("message"));
        model.addAttribute("error", session.getAttribute("error"));

        session.removeAttribute("success");
        session.removeAttribute("message");
        session.removeAttribute("error");
        
        return "public/login";
    }

    @PostMapping("login")
    public String handleLogin(HttpSession session, Model model, @RequestParam String email, @RequestParam String password) {
        Contributor contributor = contributorRepo.findByEmailIgnoreCase(email);
        if (contributor != null) {
            if (contributor.getPassword().equals(password)) {
                session.setAttribute("contributor", contributor.getId());
                return "redirect:/contributor";
            } else {
                model.addAttribute("error", "Incorrect Password");
            }
        } else {
            model.addAttribute("error", "Invalid Email Address");
        }
        model.addAttribute("email", email);
        return "public/login";
    }
}
