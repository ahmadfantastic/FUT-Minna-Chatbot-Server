/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fut.chatbot.controlller;

import com.fut.chatbot.model.Answer;
import com.fut.chatbot.model.Ask;
import com.fut.chatbot.model.Question;
import com.fut.chatbot.model.User;
import com.fut.chatbot.model.Message;
import com.fut.chatbot.repo.AnswerRepo;
import com.fut.chatbot.repo.AskRepo;
import com.fut.chatbot.repo.ContributorRepo;
import com.fut.chatbot.repo.ExtraRepo;
import com.fut.chatbot.repo.FeedbackRepo;
import com.fut.chatbot.repo.OptionRepo;
import com.fut.chatbot.repo.PreferenceRepo;
import com.fut.chatbot.repo.PreferenceValueRepo;
import com.fut.chatbot.repo.QuestionRepo;
import com.fut.chatbot.repo.QuestionTagRepo;
import com.fut.chatbot.repo.TagRepo;
import com.fut.chatbot.repo.UserPreferenceRepo;
import com.fut.chatbot.repo.UserRepo;
import com.fut.chatbot.search.HibernateSearchService;
import com.fut.chatbot.search.SearchItem;
import com.fut.chatbot.util.Constants;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.fut.chatbot.repo.MessageRepo;

/**
 *
 * @author ahmad
 */
@Controller
public class WebSocketController {

    @Autowired
    AnswerRepo answerRepo;
    @Autowired
    AskRepo askRepo;
    @Autowired
    ContributorRepo contributorRepo;
    @Autowired
    ExtraRepo extraRepo;
    @Autowired
    FeedbackRepo feedbackRepo;
    @Autowired
    OptionRepo optionRepo;
    @Autowired
    PreferenceRepo preferenceRepo;
    @Autowired
    PreferenceValueRepo preferenceValueRepo;
    @Autowired
    QuestionRepo questionRepo;
    @Autowired
    QuestionTagRepo questionTagRepo;
    @Autowired
    TagRepo tagRepo;
    @Autowired
    UserPreferenceRepo userPreferenceRepo;
    @Autowired
    UserRepo userRepo;
    @Autowired
    private MessageRepo userMessageRepo;
    @Autowired
    private HibernateSearchService searchService;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private SimpUserRegistry simpUserRegistry;

    @MessageMapping("/send")
    public void sendSpecific(@Payload Ask ask, @Header("simpSessionId") String sessionId, Principal principal) throws Exception {
        String phone = principal.getName();
        User user = userRepo.findByPhone(phone);
        System.out.println("Phone=" + phone + ", Ask=" + ask.getText());
        if (phone != null && user != null) {
            List<SearchItem> searchItems = searchService.search(ask.getText());
            Question question = analyzeResult(searchItems);
            Answer answer = question == null ? null : question.getAnswer();

            ask.setQuestion(question);
            ask.setUser(user);
            askRepo.save(ask);

            Message message = new Message();
            message.setTime(new Date());
            message.setUser(user);
            message.setAnswer(answer);
            message = userMessageRepo.save(message);
            message.getUser().setFeedbacks(new ArrayList<>());

            simpMessagingTemplate.convertAndSendToUser(phone, "/reply",Constants.GSON_EXPOSE.toJson(message));
            //simpMessagingTemplate.convertAndSend("/queue", out);
        }
        //System.out.println(simpUserRegistry.getUser(username));
    }

    @RequestMapping("test/{phone}")
    @ResponseBody
    public String test(@PathVariable("phone") String phone, @RequestParam String text) {
        User user = userRepo.findByPhone(phone);
        Ask ask = new Ask();
        ask.setText(text);
        if (phone != null && user != null) {
            List<SearchItem> searchItems = searchService.search(ask.getText());
            Question question = analyzeResult(searchItems);

            Message message = new Message();
            message.setAnswer(question == null ? null : question.getAnswer());
            message.setTime(new Date());
            message.setUser(user);
            message = userMessageRepo.save(message);

            return Constants.GSON_EXPOSE.toJson(message);
        }
        return null;
    }
    
    @RequestMapping("send/{phone}")
    @ResponseBody
    public String send(@PathVariable("phone") String phone, @RequestParam String text) {
        User user = userRepo.findByPhone(phone);
        Ask ask = new Ask();
        ask.setText(text);
        System.out.println("Phone=" + phone + ", Ask=" + ask.getText());
        if (phone != null && user != null) {
            List<SearchItem> searchItems = searchService.search(ask.getText());
            Question question = analyzeResult(searchItems);
            Answer answer = question == null ? null : question.getAnswer();

            Message message = new Message();
            message.setTime(new Date());
            message.setUser(user);
            message.setAnswer(answer);
            message.getUser().setFeedbacks(new ArrayList<>());

            simpMessagingTemplate.convertAndSendToUser(phone, "/reply",Constants.GSON_EXPOSE.toJson(message));
            //simpMessagingTemplate.convertAndSend("/queue", out);
        }
        return "sent";
    }

    private Question analyzeResult(List<SearchItem> searchItems) {
        //TODO Analyze
        if (searchItems == null || searchItems.isEmpty()) {
            return null;
        }
        return searchItems.get(0).getQuestion();
    }

    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void reportCurrentTime() {
        //TODO Delete Expired Message Pool
        System.out.println("Checking for expires broadcast messages: " + new Date());
    }
}
