/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fut.chatbot.controlller;

import com.fut.chatbot.model.*;
import com.fut.chatbot.repo.*;
import com.fut.chatbot.search.*;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.fut.chatbot.repo.MessageRepo;
import com.fut.chatbot.repo.PollRepo;
import java.util.stream.Collectors;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;

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
    MessageRepo messageRepo;
    @Autowired
    UserRepo userRepo;
    @Autowired
    UserTagRepo userTagRepo;
    @Autowired
    PollRepo pollRepo;
    @Autowired
    BroadcastRepo broadcastRepo;

    @Autowired
    private HibernateSearchService searchService;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/send")
    public void recieveAsk(@Payload Ask ask, @Header("simpSessionId") String sessionId, Principal principal) throws Exception {
        String phone = principal.getName();
        User user = userRepo.findByPhone(phone);
        System.out.println("Phone=" + phone + ", Ask=" + ask.getText());
        if (phone != null && user != null) {
            List<SearchItem> searchItems = searchService.search(ask.getText());
            Question question = analyzeResult(user, searchItems);
            Answer answer = question == null ? null : question.getAnswer();

            if (question != null) {
                checkForAutomaticSubscription(user, question);
            }

            ask.setQuestion(question);
            ask.setUser(user);
            ask.setAccuracy(Ask.AskAccuracy.UNKNOWN);
            ask.setAskTime(new Date());
            askRepo.save(ask);

            Message message = new Message();
            message.setType(Message.MessageType.ANSWER);
            message.setTime(new Date());
            message.setUser(user);
            message.setAnswer(answer);
            message = messageRepo.save(message);
            message.getUser().setFeedbacks(new ArrayList<>());

            answer = refineAnswer(question, answer);
            message.setAnswer(answer);

            String jsonMessage = Constants.GSON_EXPOSE.toJson(message);
            simpMessagingTemplate.convertAndSendToUser(phone, "/reply", jsonMessage);
        }
    }

    @MessageMapping("/delivery")
    public void receiveDelivery(@Payload int messageId, @Header("simpSessionId") String sessionId, Principal principal) throws Exception {
        String phone = principal.getName();
        User user = userRepo.findByPhone(phone);
        System.out.println("Phone=" + phone + ", MessageId=" + messageId);
        if (user != null && messageRepo.existsById(messageId)) {
            Message message = messageRepo.findById(messageId).get();
            if (message.getUser().equals(user)) {
                messageRepo.delete(message);
                System.out.println("Message Deleted");
            }
        }
    }

    @MessageMapping("/online")
    public void receiveOnlineUpdate(@Header("simpSessionId") String sessionId, Principal principal) throws Exception {
        String phone = principal.getName();
        User user = userRepo.findByPhone(phone);
        System.out.println("Phone=" + phone + " is online");
        if (user != null) {
            sendOfflineMessages(user);
        }
    }

    @Async
    private void sendOfflineMessages(User user) {
        for (Message message : messageRepo.findAllByUser(user)) {
            String jsonMessage = Constants.GSON_EXPOSE.toJson(message);
            simpMessagingTemplate.convertAndSendToUser(user.getPhone(), "/reply", jsonMessage);
        }
    }

    private Answer refineAnswer(Question question, Answer answer) {
        if (question == null) {
            answer = new Answer();
            answer.setBody(Constants.MESSAGE_QUESTION_UNKNOWN);
            answer.setExtras(new ArrayList<>());
            answer.setOptions(new ArrayList<>());
        } else if (question.getAnswer() == null) {
            answer = new Answer();
            answer.setBody(Constants.MESSAGE_QUESTION_NO_ANSWER);
            answer.setExtras(new ArrayList<>());
            answer.setOptions(new ArrayList<>());
        } else {
            Date now = new Date();
            long hours = (now.getTime() - question.getSetupTime().getTime()) / (1000 * 60 * 60);
            long days = hours / 24;
            switch (question.getExpiry()) {
                case HOUR:
                    if (hours >= 1) {
                        answer.setBody(Constants.MESSAGE_QUESTION_EXPIRED_ANSWER + answer.getBody());
                    }
                    break;
                case DAY:
                    if (days >= 1) {
                        answer.setBody(Constants.MESSAGE_QUESTION_EXPIRED_ANSWER + answer.getBody());
                    }
                    break;
                case WEEK:
                    if (days >= 7) {
                        answer.setBody(Constants.MESSAGE_QUESTION_EXPIRED_ANSWER + answer.getBody());
                    }
                    break;
                case MONTH:
                    if (days >= 30) {
                        answer.setBody(Constants.MESSAGE_QUESTION_EXPIRED_ANSWER + answer.getBody());
                    }
                    break;
                case SEMESTER:
                    if (days >= 120) {
                        answer.setBody(Constants.MESSAGE_QUESTION_EXPIRED_ANSWER + answer.getBody());
                    }
                    break;
                case SESSION:
                    if (days >= 240) {
                        answer.setBody(Constants.MESSAGE_QUESTION_EXPIRED_ANSWER + answer.getBody());
                    }
                    break;
                case NEVER:
                    if (hours >= 1) {
                        answer.setBody(Constants.MESSAGE_QUESTION_EXPIRED_ANSWER + answer.getBody());
                    }
                    break;
            }
        }
        return answer;
    }

    private Question analyzeResult(User user, List<SearchItem> searchItems) {
        if (searchItems == null || searchItems.isEmpty() || searchItems.get(0).getScore() < Constants.QUESTION_MINIMUM_SCORE) {
            return null;
        }

        if ((searchItems.size() == 1) || (searchItems.get(0).getScore() - searchItems.get(1).getScore()) > Constants.QUESTION_SCORE_DIFFERENCE) {
            return searchItems.get(0).getQuestion();
        }
        double bestScore = searchItems.get(0).getScore();
        ArrayList<SearchItem> criticalCandidates = (ArrayList<SearchItem>) searchItems.stream().filter((searchItem) -> {
            return searchItem.getScore() + Constants.QUESTION_SCORE_DIFFERENCE >= bestScore;
        });

        SearchItem bestCandidate = searchItems.get(0);
        double bestCriticalScore = -50000;
        for (int i = 0; i < criticalCandidates.size(); i++) {
            SearchItem criticalCandidate = criticalCandidates.get(i);
            double score = criticalCandidate.getScore();
            score += Constants.QUESTION_WRONG_SCORE * askRepo.countByQuestionAndAccuracy(criticalCandidate.getQuestion(), Ask.AskAccuracy.WRONG);
            score += Constants.QUESTION_RIGHT_SCORE * askRepo.countByQuestionAndAccuracy(criticalCandidate.getQuestion(), Ask.AskAccuracy.RIGHT);

            List<Tag> questionTags = criticalCandidate.getQuestion().getTags().stream().map(QuestionTag::getTag).collect(Collectors.toList());
            for (UserTag userTag : user.getTags()) {
                if (questionTags.contains(userTag.getTag())) {
                    score += Constants.QUESTION_TAG_SCORE;
                }
            }
            bestCriticalScore += askRepo.countByQuestion(criticalCandidate.getQuestion()) * Constants.QUESTION_COUNT_SCORE;
            if (score > bestCriticalScore) {
                bestCriticalScore = score;
                bestCandidate = criticalCandidate;
            }
        }

        return bestCandidate.getQuestion();
    }

    @Async
    private void checkForAutomaticSubscription(User user, @NonNull Question question) {
        List<Tag> userTags = user.getTags().stream().map(UserTag::getTag).collect(Collectors.toList());
        List<QuestionTag> tagsToCheck = question.getTags().stream().filter((t) -> {
            return !userTags.contains(t.getTag());//Note: does not contains
        }).collect(Collectors.toList());

        for (QuestionTag questionTag : tagsToCheck) {
            Date checkTime = new Date();
            checkTime.setTime(checkTime.getTime() - (Constants.TAG_SUBCRIBTION_ASK_DAYS * 60 * 60 * 24));
            long askCount = askRepo.countAskForSubscription(user, checkTime, question, questionTag);
            if (askCount >= Constants.TAG_SUBCRIPTION_ASK_COUNT) {
                UserTag userTag = new UserTag();
                userTag.setUser(user);
                userTag.setTag(questionTag.getTag());
                userTagRepo.save(userTag);
            }
        }
    }

    @Scheduled(fixedRate = Constants.BROKER_SCHEDULER_HOUR_INTERVAL * 60 * 60 * 1000)
    public void schedulerJob() {
        Date now = new Date();
        System.out.println("Message Broker cheking expire message queue: " + now);
        int deleteCount = 0;
        for (Message message : messageRepo.findAll()) {
            long hours = (now.getTime() - message.getTime().getTime()) / (1000 * 60 * 60);
            long days = hours / 24;
            if (days >= Constants.MESSAGE_EXPIRY_DAYS) {
                deleteCount++;
                messageRepo.delete(message);
            }
        }
        deleteCount += checkBroadcastsExpiry();
        deleteCount += checkPollsExpiry();

        System.out.println("Message Broker has removed " + deleteCount + " messages from the message pool");
    }

    private int checkBroadcastsExpiry() {
        int deleteCount = 0;
        Date now = new Date();
        for (Broadcast broadcast : broadcastRepo.findAllByStatus(Broadcast.BroadcastStatus.SENT)) {
            long hours = (now.getTime() - broadcast.getSetupTime().getTime()) / (1000 * 60 * 60);
            long days = hours / 24;
            switch (broadcast.getExpiry()) {
                case DAY:
                    if (days >= 1) {
                        deleteCount++;
                        messageRepo.deleteAll(messageRepo.findAllByBroadcast(broadcast));
                    }
                    break;
                case HOUR:
                    if (hours >= 1) {
                        deleteCount++;
                        messageRepo.deleteAll(messageRepo.findAllByBroadcast(broadcast));
                    }
                    break;
                case HOUR_2:
                    if (hours >= 2) {
                        deleteCount++;
                        messageRepo.deleteAll(messageRepo.findAllByBroadcast(broadcast));
                    }
                    break;
                case WEEK:
                    if (days >= 7) {
                        deleteCount++;
                        messageRepo.deleteAll(messageRepo.findAllByBroadcast(broadcast));
                    }
                    break;
            }
        }
        return deleteCount;
    }

    private int checkPollsExpiry() {
        int deleteCount = 0;
        Date now = new Date();
        for (Poll poll : pollRepo.findAllByStatus(Poll.PollStatus.PROGRESS)) {
            long hours = (now.getTime() - poll.getSetupTime().getTime()) / (1000 * 60 * 60);
            long days = hours / 24;
            boolean expired = false;
            switch (poll.getExpiry()) {
                case DAY:
                    if (days >= 1) {
                        deleteCount++;
                        expired = true;
                        messageRepo.deleteAll(messageRepo.findAllByPoll(poll));
                    }
                    break;
                case DAY_3:
                    if (days >= 2) {
                        deleteCount++;
                        expired = true;
                        messageRepo.deleteAll(messageRepo.findAllByPoll(poll));
                    }
                    break;
                case WEEK:
                    if (days >= 7) {
                        deleteCount++;
                        expired = true;
                        messageRepo.deleteAll(messageRepo.findAllByPoll(poll));
                    }
                    break;
            }
            if (expired) {
                poll.setStatus(Poll.PollStatus.FINISHED);
                pollRepo.save(poll);
            }
        }
        return deleteCount;
    }

    @RequestMapping("test/{phone}")
    @ResponseBody
    public String test(@PathVariable("phone") String phone, @RequestParam String text) {
        User user = userRepo.findByPhone(phone);
        Ask ask = new Ask();
        ask.setText(text);
        if (phone != null && user != null) {
            List<SearchItem> searchItems = searchService.search(ask.getText());
            Question question = analyzeResult(user, searchItems);

            Message message = new Message();
            message.setAnswer(question == null ? null : question.getAnswer());
            message.setTime(new Date());
            message.setUser(user);
            message = messageRepo.save(message);

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
            Question question = analyzeResult(user, searchItems);
            Answer answer = question == null ? null : question.getAnswer();

            Message message = new Message();
            message.setTime(new Date());
            message.setUser(user);
            message.setAnswer(answer);
            message.getUser().setFeedbacks(new ArrayList<>());

            simpMessagingTemplate.convertAndSendToUser(phone, "/reply", Constants.GSON_EXPOSE.toJson(message));
            //simpMessagingTemplate.convertAndSend("/queue", out);
        }
        return "sent";
    }

}
