/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fut.chatbot.controlller;

import com.fut.chatbot.repo.*;
import com.fut.chatbot.model.*;
import com.fut.chatbot.util.Constants;
import com.fut.chatbot.util.ImageSaver;
import com.fut.chatbot.util.PatternChecker;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author ahmad
 */
@Controller
@RequestMapping("contributor/save")
public class SaveController {

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
    ContributorTagRepo contributorTagRepo;
    @Autowired
    BroadcastRepo broadcastRepo;
    @Autowired
    BroadcastTagRepo broadcastTagRepo;
    @Autowired
    PollTagRepo pollTagRepo;
    @Autowired
    PollRepo pollRepo;
    @Autowired
    PollItemRepo pollItemRepo;
    @Autowired
    MessageRepo messageRepo;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @PostMapping("contributor")
    public String saveContributor(HttpSession session, Model model, @ModelAttribute Contributor newContributor, @RequestParam(name = "image", required = false) MultipartFile image) throws IOException {
        int id = (Integer) (session.getAttribute("contributor") != null ? session.getAttribute("contributor") : -1);
        if (id <= 0 && !contributorRepo.existsById(id)) {
            session.setAttribute("error", "Login Required");
            return "redirect:/login";
        }
        if (contributorRepo.findById(id).get().getType() != Contributor.ContributorType.SUPER) {
            session.setAttribute("error", "Access Denied");
            return "redirect:/login";
        }

        if (contributorRepo.existsById(newContributor.getId())) {
            Contributor initContributor = contributorRepo.findById(newContributor.getId()).get();
            newContributor.setStatus(initContributor.getStatus());
        } else {
            if (PatternChecker.checkEmail(newContributor.getEmail())) {
                newContributor.setStatus(Contributor.ContributorStatus.ACTIVE);
                newContributor.setRegistrationDate(new Date());
                newContributor.setPassword(Constants.DEFAULT_PASSWORD);
            } else {
                session.setAttribute("error", "Invalid Email Address");
                return "redirect:/contributor/contributors";
            }
        }

        if (image != null && !image.isEmpty()) {
            String imageUrl = "contributor/" + System.currentTimeMillis() + "-" + newContributor.getFirstName() + ".png";
            File imageFile = new File(Constants.IMAGE_BASE_PATH + imageUrl);
            image.transferTo(imageFile);
            if (Constants.IS_ONLINE) {
                newContributor.setImageUrl(ImageSaver.getInstance().saveImage(imageFile));
            } else {
                newContributor.setImageUrl(imageUrl);
            }
        }

        Contributor savedContributor = contributorRepo.save(newContributor);

        if (savedContributor.getId() > 0) {
            String message = savedContributor.getId() == newContributor.getId()
                    ? "Contributor Updated Successfully"
                    : "Contributor Added Successfully";
            session.setAttribute("success", message);
        } else {
            session.setAttribute("error", "Invalid Field(s)");
        }
        return "redirect:/contributor/contributors";
    }

    @RequestMapping("contributor/update_status/{id}/{action}")
    public String updateContributorStatus(HttpSession session, Model model, @PathVariable("id") int contributorId, @PathVariable("action") int action) {
        int id = (Integer) (session.getAttribute("contributor") != null ? session.getAttribute("contributor") : -1);
        if (id <= 0 && !contributorRepo.existsById(id)) {
            session.setAttribute("error", "Login Required");
            return "redirect:/login";
        }
        Contributor contributor = contributorRepo.findById(id).get();
        if (contributor.getType() != Contributor.ContributorType.SUPER) {
            session.setAttribute("error", "Access Denied");
            return "redirect:/contributor/contributors";
        }
        if (contributorRepo.existsById(contributorId)) {
            Contributor toUpdateContributor = contributorRepo.findById(contributorId).get();
            if (action == 1) {
                toUpdateContributor.setStatus(Contributor.ContributorStatus.ACTIVE);
            } else if (action == 2) {
                toUpdateContributor.setStatus(Contributor.ContributorStatus.BLOCKED);
            }
            contributorRepo.save(contributor);
            session.setAttribute("success", "Status Changed Successfully");
        } else {
            session.setAttribute("error", "Invalid Field(s)");
        }
        return "redirect:/contributor/contributors";
    }

    @PostMapping("preference")
    public String savePreference(HttpSession session, Model model, @ModelAttribute Preference newPreference) {
        int id = (Integer) (session.getAttribute("contributor") != null ? session.getAttribute("contributor") : -1);
        if (id <= 0 && !contributorRepo.existsById(id)) {
            session.setAttribute("error", "Login Required");
            return "redirect:/login";
        }

        Preference savedPreference = preferenceRepo.save(newPreference);
        if (savedPreference.getId() > 0) {
            String message = savedPreference.getId() == newPreference.getId()
                    ? "Preference Updated Successfully"
                    : "Preference Added Successfully";
            session.setAttribute("success", message);
        } else {
            session.setAttribute("error", "Invalid Field(s)");
        }
        return "redirect:/contributor/preferences";
    }

    @PostMapping("preference_value")
    public String savePreferenceValue(HttpSession session, Model model, @ModelAttribute PreferenceValue newPreferenceValue) {
        int id = (Integer) (session.getAttribute("contributor") != null ? session.getAttribute("contributor") : -1);
        if (id <= 0 && !contributorRepo.existsById(id)) {
            session.setAttribute("error", "Login Required");
            return "redirect:/login";
        }
        Contributor contributor = contributorRepo.findById(id).get();
        if (contributor.getType() != Contributor.ContributorType.SUPER) {
            session.setAttribute("error", "Access Denied");
            return "redirect:/contributor/dashboard";
        }
        PreferenceValue savedPreferenceValue = preferenceValueRepo.save(newPreferenceValue);
        if (savedPreferenceValue.getId() > 0) {
            String message = savedPreferenceValue.getId() == newPreferenceValue.getId()
                    ? "Preference Value Updated Successfully"
                    : "Preference Value Added Successfully";
            session.setAttribute("success", message);
        } else {
            session.setAttribute("error", "Invalid Field(s)");
        }
        return "redirect:/contributor/preferences";
    }

    @PostMapping("tag")
    public String saveTag(HttpSession session, Model model, @ModelAttribute Tag newTag) {
        int id = (Integer) (session.getAttribute("contributor") != null ? session.getAttribute("contributor") : -1);
        if (id <= 0 && !contributorRepo.existsById(id)) {
            session.setAttribute("error", "Login Required");
            return "redirect:/login";
        }
        if (tagRepo.findByName(newTag.getName()) != null) {
            session.setAttribute("error", "Tag Already Exists");
            return "redirect:/contributor/tags";
        }

        Tag savedTag = tagRepo.save(newTag);
        if (savedTag.getId() > 0) {
            String message = savedTag.getId() == newTag.getId()
                    ? "Tag Updated Successfully"
                    : "Tag Added Successfully";
            session.setAttribute("success", message);
        } else {
            session.setAttribute("error", "Invalid Field(s)");
        }
        return "redirect:/contributor/tags";
    }

    @PostMapping("question")
    public String saveQuestion(HttpSession session, Model model, @ModelAttribute Question newQuestion) {
        int id = (Integer) (session.getAttribute("contributor") != null ? session.getAttribute("contributor") : -1);
        if (id <= 0 && !contributorRepo.existsById(id)) {
            session.setAttribute("error", "Login Required");
            return "redirect:/login";
        }
        Contributor contributor = contributorRepo.findById(id).get();
        if (questionRepo.existsById(newQuestion.getId())) {
            Question initQuestion = questionRepo.findById(newQuestion.getId()).get();
            if (contributor.getType() != Contributor.ContributorType.SUPER && initQuestion.getContributor() != contributor) {
                session.setAttribute("error", "Access Denied");
                return "redirect:/contributor/questions";
            }
            newQuestion.setAnswer(initQuestion.getAnswer());
            newQuestion.setStatus(initQuestion.getStatus());
            newQuestion.setContributor(initQuestion.getContributor());
        } else {
            newQuestion.setStatus(Question.QuestionStatus.CREATED);
            newQuestion.setContributor(contributor);
        }
        Question savedQuestion = questionRepo.save(newQuestion);
        if (savedQuestion.getId() > 0) {
            String message = savedQuestion.getId() == newQuestion.getId()
                    ? "Question Updated Successfully"
                    : "Question Added Successfully";
            session.setAttribute("success", message);
        } else {
            session.setAttribute("error", "Invalid Field(s)");
        }
        return "redirect:/contributor/questions";
    }

    @PostMapping("broadcast")
    public String saveBroadcast(HttpSession session, Model model, @ModelAttribute Broadcast newBroadcast) {
        int id = (Integer) (session.getAttribute("contributor") != null ? session.getAttribute("contributor") : -1);
        if (id <= 0 && !contributorRepo.existsById(id)) {
            session.setAttribute("error", "Login Required");
            return "redirect:/login";
        }
        Contributor contributor = contributorRepo.findById(id).get();
        if (broadcastRepo.existsById(newBroadcast.getId())) {
            Broadcast initBroadcast = broadcastRepo.findById(newBroadcast.getId()).get();
            if (initBroadcast.getContributor() != contributor || initBroadcast.getStatus() != Broadcast.BroadcastStatus.CREATED) {
                session.setAttribute("error", "Access Denied");
                return "redirect:/contributor/broadcasts";
            }
            newBroadcast.setStatus(initBroadcast.getStatus());
            newBroadcast.setContributor(initBroadcast.getContributor());
        } else {
            newBroadcast.setStatus(Broadcast.BroadcastStatus.CREATED);
            newBroadcast.setContributor(contributor);
        }
        Broadcast savedBroadcast = broadcastRepo.save(newBroadcast);
        if (savedBroadcast.getId() > 0) {
            String message = savedBroadcast.getId() == newBroadcast.getId()
                    ? "Broadcast Updated Successfully"
                    : "Broadcast Created Successfully";
            session.setAttribute("success", message);
        } else {
            session.setAttribute("error", "Invalid Field(s)");
        }
        return "redirect:/contributor/broadcasts";
    }

    @PostMapping("poll")
    public String savePoll(HttpSession session, Model model, @ModelAttribute Poll newPoll) {
        int id = (Integer) (session.getAttribute("contributor") != null ? session.getAttribute("contributor") : -1);
        if (id <= 0 && !contributorRepo.existsById(id)) {
            session.setAttribute("error", "Login Required");
            return "redirect:/login";
        }
        Contributor contributor = contributorRepo.findById(id).get();
        if (pollRepo.existsById(newPoll.getId())) {
            Poll initPoll = pollRepo.findById(newPoll.getId()).get();
            if (initPoll.getContributor() != contributor || initPoll.getStatus() != Poll.PollStatus.CREATED) {
                session.setAttribute("error", "Access Denied");
                return "redirect:/contributor/polls";
            }
            newPoll.setStatus(initPoll.getStatus());
            newPoll.setContributor(initPoll.getContributor());
        } else {
            newPoll.setStatus(Poll.PollStatus.CREATED);
            newPoll.setContributor(contributor);
        }
        Poll savedPoll = pollRepo.save(newPoll);
        if (savedPoll.getId() > 0) {
            String message = savedPoll.getId() == newPoll.getId()
                    ? "Poll Updated Successfully"
                    : "Poll Created Successfully";
            session.setAttribute("success", message);
        } else {
            session.setAttribute("error", "Invalid Field(s)");
        }
        return "redirect:/contributor/polls";
    }

    @PostMapping("answer/{id}")
    public String saveAnswer(HttpSession session, Model model, @ModelAttribute Answer newAnswer, @PathVariable("id") int initQuestionId) {
        int id = (Integer) (session.getAttribute("contributor") != null ? session.getAttribute("contributor") : -1);
        if (id <= 0 && !contributorRepo.existsById(id)) {
            session.setAttribute("error", "Login Required");
            return "redirect:/login";
        }
        Contributor contributor = contributorRepo.findById(id).get();
        if (questionRepo.existsById(initQuestionId)) {
            Question initQuestion = questionRepo.findById(initQuestionId).get();
            if (answerRepo.existsById(newAnswer.getId())) {
                Answer initAnswer = answerRepo.findById(newAnswer.getId()).get();
                if (contributor.getType() != Contributor.ContributorType.SUPER
                        && !(initAnswer.getStatus() == Answer.AnswerStatus.CREATED
                        && initAnswer.getContributor() == contributor)) {
                    session.setAttribute("error", "Access Denied");
                    return "redirect:/contributor/questions";
                }

                if (initAnswer.getPreference() != null) {
                    session.setAttribute("error", "Preference Answer cannot be editted");
                    return "redirect:/contributor/questions";
                }
                newAnswer.setStatus(initAnswer.getStatus());
                newAnswer.setQuestion(initAnswer.getQuestion());
                newAnswer.setOption(initAnswer.getOption());
                newAnswer.setContributor(initAnswer.getContributor());
            } else {
                if (newAnswer.getPreference() != null && newAnswer.getPreference().getType() == Preference.PreferenceType.USER_DEFINED) {
                    session.setAttribute("error", "Preference Answer only works with Predefined Preferences");
                    return "redirect:/contributor/questions";
                }
                newAnswer.setStatus(Answer.AnswerStatus.CREATED);
                newAnswer.setContributor(contributor);
            }

            Answer savedAnswer = answerRepo.save(newAnswer);
            if (savedAnswer.getPreference() != null) {
                Preference preference = preferenceRepo.findById(savedAnswer.getPreference().getId()).get();
                for (PreferenceValue value : preference.getValues()) {
                    Option option = new Option();
                    option.setParentAnswer(savedAnswer);
                    option.setPreferenceValue(value);
                    option.setText("");
                    optionRepo.save(option);
                }
            }
            if (savedAnswer.getId() > 0) {
                String message = savedAnswer.getId() == newAnswer.getId()
                        ? "Answer Updated Successfully"
                        : "Answer Added Successfully";
                session.setAttribute("success", message);
            } else {
                session.setAttribute("error", "Invalid Field(s)");
                return "redirect:/contributor";
            }

            if (savedAnswer.getQuestion().getStatus() == Question.QuestionStatus.APPROVED) {
                return "redirect:/contributor/questions";
            } else {
                return "redirect:/contributor/answers/" + initQuestion.getId();
            }
        } else {
            session.setAttribute("error", "Invalid Field(s)");
            return "redirect:/contributor/questions";
        }
    }

    @PostMapping("question_tag")
    public String saveQuestionTag(HttpSession session, Model model, @ModelAttribute QuestionTag newQuestionTag, @RequestParam("tag_name") String tagName) {
        int id = (Integer) (session.getAttribute("contributor") != null ? session.getAttribute("contributor") : -1);
        if (id <= 0 && !contributorRepo.existsById(id)) {
            session.setAttribute("error", "Login Required");
            return "redirect:/login";
        }
        Contributor contributor = contributorRepo.findById(id).get();
        if (newQuestionTag.getQuestion() != null && newQuestionTag.getQuestion().getContributor() != null) {
            if (contributor.getType() != Contributor.ContributorType.SUPER && newQuestionTag.getQuestion().getContributor() != contributor) {
                session.setAttribute("error", "Access Denied");
                return "redirect:/contributor/questions";
            }
            Tag tag = tagRepo.findByName(tagName);
            if (tag == null) {
                session.setAttribute("error", "Tag does not exist");
                return "redirect:/contributor/questions";
            }
            if (newQuestionTag.getQuestion().getTags().contains(newQuestionTag)) {
                session.setAttribute("error", "Tag Already in the question");
                return "redirect:/contributor/questions";
            }
            newQuestionTag.setTag(tag);
            QuestionTag savedQuestionTag = questionTagRepo.save(newQuestionTag);
            if (savedQuestionTag.getId() > 0) {
                String message = "Tag Added to Question Successfully";
                session.setAttribute("success", message);
            } else {
                session.setAttribute("error", "Invalid Field(s)");
            }
        } else {
            session.setAttribute("error", "Invalid Field(s)");
            return "redirect:/contributor/questions";
        }
        return "redirect:/contributor/questions";
    }

    @PostMapping("broadcast_tag")
    public String saveBroadcastTag(HttpSession session, Model model, @ModelAttribute BroadcastTag newBroadcastTag, @RequestParam("tag_name") String tagName) {
        int id = (Integer) (session.getAttribute("contributor") != null ? session.getAttribute("contributor") : -1);
        if (id <= 0 && !contributorRepo.existsById(id)) {
            session.setAttribute("error", "Login Required");
            return "redirect:/login";
        }
        Contributor contributor = contributorRepo.findById(id).get();
        if (newBroadcastTag.getBroadcast() != null && newBroadcastTag.getBroadcast().getContributor() != null) {
            if (newBroadcastTag.getBroadcast().getContributor() != contributor) {
                session.setAttribute("error", "Access Denied");
                return "redirect:/contributor/broadcasts";
            }
            Tag tag = tagRepo.findByName(tagName);
            if (tag == null) {
                session.setAttribute("error", "Tag does not exist");
                return "redirect:/contributor/broadcasts";
            }
            if (newBroadcastTag.getBroadcast().getTags().contains(newBroadcastTag)) {
                session.setAttribute("error", "Tag Already in the broadcast");
                return "redirect:/contributor/broadcasts";
            }
            newBroadcastTag.setTag(tag);
            BroadcastTag savedBroadcastTag = broadcastTagRepo.save(newBroadcastTag);
            if (savedBroadcastTag.getId() > 0) {
                String message = "Tag Added to Broadcast Successfully";
                session.setAttribute("success", message);
            } else {
                session.setAttribute("error", "Invalid Field(s)");
            }
        } else {
            session.setAttribute("error", "Invalid Field(s)");
            return "redirect:/contributor/broadcasts";
        }
        return "redirect:/contributor/broadcasts";
    }

    @PostMapping("poll_tag")
    public String savePollTag(HttpSession session, Model model, @ModelAttribute PollTag newPollTag, @RequestParam("tag_name") String tagName) {
        int id = (Integer) (session.getAttribute("contributor") != null ? session.getAttribute("contributor") : -1);
        if (id <= 0 && !contributorRepo.existsById(id)) {
            session.setAttribute("error", "Login Required");
            return "redirect:/login";
        }
        Contributor contributor = contributorRepo.findById(id).get();
        if (newPollTag.getPoll() != null && newPollTag.getPoll().getContributor() != null) {
            if (newPollTag.getPoll().getContributor() != contributor) {
                session.setAttribute("error", "Access Denied");
                return "redirect:/contributor/polls";
            }
            Tag tag = tagRepo.findByName(tagName);
            if (tag == null) {
                session.setAttribute("error", "Tag does not exist");
                return "redirect:/contributor/polls";
            }
            if (newPollTag.getPoll().getTags().contains(newPollTag)) {
                session.setAttribute("error", "Tag Already in the poll");
                return "redirect:/contributor/polls";
            }
            newPollTag.setTag(tag);
            PollTag savedPollTag = pollTagRepo.save(newPollTag);
            if (savedPollTag.getId() > 0) {
                String message = "Tag Added to Poll Successfully";
                session.setAttribute("success", message);
            } else {
                session.setAttribute("error", "Invalid Field(s)");
            }
        } else {
            session.setAttribute("error", "Invalid Field(s)");
            return "redirect:/contributor/polls";
        }
        return "redirect:/contributor/polls";
    }

    @PostMapping("contributor_tag")
    public String saveContributorTag(HttpSession session, Model model, @ModelAttribute ContributorTag newContributorTag, @RequestParam("tag_name") String tagName) {
        int id = (Integer) (session.getAttribute("contributor") != null ? session.getAttribute("contributor") : -1);
        if (id <= 0 && !contributorRepo.existsById(id)) {
            session.setAttribute("error", "Login Required");
            return "redirect:/login";
        }
        Contributor contributor = contributorRepo.findById(id).get();
        if (newContributorTag.getContributor() != null) {
            if (contributor.getType() != Contributor.ContributorType.SUPER) {
                session.setAttribute("error", "Access Denied");
                return "redirect:/contributor/contributors";
            }
            Tag tag = tagRepo.findByName(tagName);
            if (tag == null) {
                session.setAttribute("error", "Tag does not exist");
                return "redirect:/contributor/contributors";
            }
            if (newContributorTag.getContributor().getTags().contains(newContributorTag)) {
                session.setAttribute("error", "Tag Already in the contributor");
                return "redirect:/contributor/contributors";
            }
            newContributorTag.setTag(tag);
            ContributorTag savedContributorTag = contributorTagRepo.save(newContributorTag);
            if (savedContributorTag.getId() > 0) {
                String message = "Tag Added to Contributor Successfully";
                session.setAttribute("success", message);
            } else {
                session.setAttribute("error", "Invalid Field(s)");
            }
        } else {
            session.setAttribute("error", "Invalid Field(s)");
            return "redirect:/contributor/contributors";
        }
        return "redirect:/contributor/contributors";
    }

    @RequestMapping("update_question_status/{id}/{status}")
    public String updateQuestionStatus(HttpSession session, Model model, @PathVariable("id") int questionId, @PathVariable("status") int status) {
        int id = (Integer) (session.getAttribute("contributor") != null ? session.getAttribute("contributor") : -1);
        if (id <= 0 && !contributorRepo.existsById(id)) {
            session.setAttribute("error", "Login Required");
            return "redirect:/login";
        }
        Contributor contributor = contributorRepo.findById(id).get();
        if (questionRepo.existsById(questionId)) {
            Question question = questionRepo.findById(questionId).get();
            switch (status) {
                case 1:
                    if (question.getStatus().equals(Question.QuestionStatus.CREATED) && question.getContributor().equals(contributor)) {
                        question.setStatus(Question.QuestionStatus.REQUESTED);
                        questionRepo.save(question);
                        session.setAttribute("success", "Question Sent for Approval !!!");
                    } else {
                        session.setAttribute("error", "Access Denied !!!");
                    }
                    break;
                case 2:
                    if (question.getStatus().equals(Question.QuestionStatus.REQUESTED) && contributor.getType() != Contributor.ContributorType.REGULAR
                            && (!question.getContributor().equals(contributor) || contributor.getType() == Contributor.ContributorType.SUPER)) {
                        question.setStatus(Question.QuestionStatus.APPROVED);
                        question.setSetupTime(new Date());
                        questionRepo.save(question);
                        session.setAttribute("success", "Question Approved !!!");
                    } else {
                        session.setAttribute("error", "Access Denied !!!");
                    }
                    break;
                case 3:
                    if (question.getStatus().equals(Question.QuestionStatus.REQUESTED) && contributor.getType() != Contributor.ContributorType.REGULAR
                            && (!question.getContributor().equals(contributor) || contributor.getType() == Contributor.ContributorType.SUPER)) {
                        question.setStatus(Question.QuestionStatus.CREATED);
                        questionRepo.save(question);
                        //TODO SEND MAIL
                        session.setAttribute("success", "Question Rejected !!!");
                    } else {
                        session.setAttribute("error", "Access Denied !!!");
                    }
                    break;
                default:
                    session.setAttribute("error", "Invalid Fields !!!");
                    break;
            }
        } else {
            session.setAttribute("error", "Invalid Field(s)");
        }
        return "redirect:/contributor/questions";
    }

    @RequestMapping("update_broadcast_status/{id}/{status}")
    public String updateBroadcastStatus(HttpSession session, Model model, @PathVariable("id") int broadcastId, @PathVariable("status") int status) {
        int id = (Integer) (session.getAttribute("contributor") != null ? session.getAttribute("contributor") : -1);
        if (id <= 0 && !contributorRepo.existsById(id)) {
            session.setAttribute("error", "Login Required");
            return "redirect:/login";
        }
        Contributor contributor = contributorRepo.findById(id).get();
        if (broadcastRepo.existsById(broadcastId)) {
            Broadcast broadcast = broadcastRepo.findById(broadcastId).get();
            switch (status) {
                case 1:
                    if (broadcast.getStatus().equals(Broadcast.BroadcastStatus.CREATED) && broadcast.getContributor().equals(contributor)) {
                        boolean fullAccess = true;
                        List<Tag> contributorsTags = contributor.getTags().stream().map(ContributorTag::getTag).collect(Collectors.toList());
                        for (BroadcastTag broadcastTag : broadcast.getTags()) {
                            if (!contributorsTags.contains(broadcastTag.getTag())) {
                                fullAccess = false;
                                break;
                            }
                        }
                        if (fullAccess || contributor.getType() == Contributor.ContributorType.SUPER) {
                            broadcast.setStatus(Broadcast.BroadcastStatus.SENT);
                            broadcast.setSetupTime(new Date());
                            broadcast = broadcastRepo.save(broadcast);
                            session.setAttribute("success", "Broadcast Sent Successully!!!");
                            sendBroadcastMessages(broadcast);
                        } else {
                            broadcast.setStatus(Broadcast.BroadcastStatus.REQUESTED);
                            broadcastRepo.save(broadcast);
                            session.setAttribute("success", "Broadcast Sent for Approval !!!");
                        }
                    } else {
                        session.setAttribute("error", "Access Denied !!!");
                    }
                    break;
                case 2:
                    if (broadcast.getStatus().equals(Broadcast.BroadcastStatus.REQUESTED) && contributor.getType() == Contributor.ContributorType.SUPER) {
                        broadcast.setStatus(Broadcast.BroadcastStatus.SENT);
                        broadcast.setSetupTime(new Date());
                        broadcast = broadcastRepo.save(broadcast);
                        session.setAttribute("success", "Broadcast Approved !!!");
                        sendBroadcastMessages(broadcast);
                    } else {
                        session.setAttribute("error", "Access Denied !!!");
                    }
                    break;
                case 3:
                    if (broadcast.getStatus().equals(Broadcast.BroadcastStatus.REQUESTED) && contributor.getType() == Contributor.ContributorType.SUPER) {
                        broadcast.setStatus(Broadcast.BroadcastStatus.CREATED);
                        broadcastRepo.save(broadcast);
                        //TODO SEND MAIL
                        session.setAttribute("success", "Broadcast Rejected !!!");
                    } else {
                        session.setAttribute("error", "Access Denied !!!");
                    }
                    break;
                default:
                    session.setAttribute("error", "Invalid Fields !!!");
                    break;
            }
        } else {
            session.setAttribute("error", "Invalid Field(s)");
        }
        return "redirect:/contributor/broadcasts";
    }

    @RequestMapping("update_poll_status/{id}/{status}")
    public String updatePollStatus(HttpSession session, Model model, @PathVariable("id") int pollId, @PathVariable("status") int status) {
        int id = (Integer) (session.getAttribute("contributor") != null ? session.getAttribute("contributor") : -1);
        if (id <= 0 && !contributorRepo.existsById(id)) {
            session.setAttribute("error", "Login Required");
            return "redirect:/login";
        }
        Contributor contributor = contributorRepo.findById(id).get();
        if (pollRepo.existsById(pollId)) {
            Poll poll = pollRepo.findById(pollId).get();
            switch (status) {
                case 1:
                    if (poll.getStatus().equals(Poll.PollStatus.CREATED) && poll.getContributor().equals(contributor)) {
                        boolean fullAccess = true;
                        List<Tag> contributorsTags = contributor.getTags().stream().map(ContributorTag::getTag).collect(Collectors.toList());
                        for (PollTag pollTag : poll.getTags()) {
                            if (!contributorsTags.contains(pollTag.getTag())) {
                                fullAccess = false;
                                break;
                            }
                        }
                        if (fullAccess || contributor.getType() == Contributor.ContributorType.SUPER) {
                            poll.setStatus(Poll.PollStatus.PROGRESS);
                            poll.setSetupTime(new Date());
                            poll = pollRepo.save(poll);
                            session.setAttribute("success", "Poll Started Successully!!!");
                            sendPollMessages(poll);
                        } else {
                            poll.setStatus(Poll.PollStatus.REQUESTED);
                            pollRepo.save(poll);
                            session.setAttribute("success", "Poll Sent for Approval !!!");
                        }
                    } else {
                        session.setAttribute("error", "Access Denied !!!");
                    }
                    break;
                case 2:
                    if (poll.getStatus().equals(Poll.PollStatus.REQUESTED) && contributor.getType() == Contributor.ContributorType.SUPER) {
                        poll.setStatus(Poll.PollStatus.PROGRESS);
                        poll.setSetupTime(new Date());
                        poll = pollRepo.save(poll);
                        session.setAttribute("success", "Poll Approved !!!");
                        sendPollMessages(poll);
                    } else {
                        session.setAttribute("error", "Access Denied !!!");
                    }
                    break;
                case 3:
                    if (poll.getStatus().equals(Poll.PollStatus.REQUESTED) && contributor.getType() == Contributor.ContributorType.SUPER) {
                        poll.setStatus(Poll.PollStatus.CREATED);
                        pollRepo.save(poll);
                        //TODO SEND MAIL
                        session.setAttribute("success", "Poll Rejected !!!");
                    } else {
                        session.setAttribute("error", "Access Denied !!!");
                    }
                    break;
                default:
                    session.setAttribute("error", "Invalid Fields !!!");
                    break;
            }
        } else {
            session.setAttribute("error", "Invalid Field(s)");
        }
        return "redirect:/contributor/polls";
    }

    @RequestMapping("update_answer_status/{id}/{status}")
    public String updateAnswerStatus(HttpSession session, Model model, @PathVariable("id") int answerId, @PathVariable("status") int status) {
        int id = (Integer) (session.getAttribute("contributor") != null ? session.getAttribute("contributor") : -1);
        if (id <= 0 && !contributorRepo.existsById(id)) {
            session.setAttribute("error", "Login Required");
            return "redirect:/login";
        }
        Contributor contributor = contributorRepo.findById(id).get();
        if (answerRepo.existsById(answerId)) {
            Answer answer = answerRepo.findById(answerId).get();
            switch (status) {
                case 1:
                    if (answer.getQuestion() != null && answer.getStatus().equals(Answer.AnswerStatus.CREATED) && answer.getContributor().equals(contributor)) {
                        answer.setStatus(Answer.AnswerStatus.REQUESTED);
                        answerRepo.save(answer);
                        session.setAttribute("success", "Answer Sent for Review !!!");
                    } else {
                        session.setAttribute("error", "Access Denied !!!");
                    }
                    break;
                default:
                    session.setAttribute("error", "Invalid Fields !!!");
                    break;
            }
        } else {
            session.setAttribute("error", "Invalid Field(s)");
        }
        return "redirect:/contributor";
    }

    @RequestMapping("approve_answer/{id}")
    public String approveAnswer(HttpSession session, Model model, @PathVariable("id") int answerId) {
        int id = (Integer) (session.getAttribute("contributor") != null ? session.getAttribute("contributor") : -1);
        if (id <= 0 && !contributorRepo.existsById(id)) {
            session.setAttribute("error", "Login Required");
            return "redirect:/login";
        }
        Contributor contributor = contributorRepo.findById(id).get();
        if (answerRepo.existsById(answerId)) {
            Answer answer = answerRepo.findById(answerId).get();
            if (answer.getQuestion() != null && answer.getQuestion().getAnswer() == null
                    && answer.getQuestion().getStatus() == Question.QuestionStatus.APPROVED
                    && answer.getStatus() == Answer.AnswerStatus.REQUESTED
                    && (contributor.getType() == Contributor.ContributorType.SUPER)
                    || contributor != answer.getContributor()) {
                answer.setApprovedBy(contributor);
                answer = answerRepo.save(answer);
                Question question = questionRepo.findById(answer.getQuestion().getId()).get();
                question.setAnswer(answer);
                question.setSetupTime(new Date());
                question = questionRepo.save(question);
                for (Answer a : answerRepo.findAllByQuestion(question)) {
                    if (a.getId() == answer.getId()) {
                        continue;
                    }
                    answerRepo.delete(a);
                }
                session.setAttribute("success", "The Answer has been Approved !!!");
            } else {
                session.setAttribute("error", "Access Denied !!!");
            }
        } else {
            session.setAttribute("error", "Invalid Field(s)");
        }

        return "redirect:/contributor/questions";
    }

    @PostMapping("extra/{id}")
    public String saveExtra(HttpSession session, Model model, @ModelAttribute Extra newExtra, @PathVariable("id") int initQuestionId) {
        int id = (Integer) (session.getAttribute("contributor") != null ? session.getAttribute("contributor") : -1);
        if (id <= 0 && !contributorRepo.existsById(id)) {
            session.setAttribute("error", "Login Required");
            return "redirect:/login";
        }
        Contributor contributor = contributorRepo.findById(id).get();
        if (questionRepo.existsById(initQuestionId)) {
            Question initQuestion = questionRepo.findById(initQuestionId).get();
            if (newExtra.getAnswer() != null && answerRepo.existsById(newExtra.getAnswer().getId())) {
                Answer initAnswer = answerRepo.findById(newExtra.getAnswer().getId()).get();
                if (contributor.getType() != Contributor.ContributorType.SUPER
                        && !(initAnswer.getStatus() == Answer.AnswerStatus.CREATED
                        && initAnswer.getContributor() == contributor)) {
                    session.setAttribute("error", "Access Denied");
                    return "redirect:/contributor/questions";
                }
                Extra savedExtra = extraRepo.save(newExtra);
                if (savedExtra.getId() > 0) {
                    String message = savedExtra.getId() == savedExtra.getId()
                            ? "Answer Extra Updated Successfully"
                            : "Answer Extra Added Successfully";
                    session.setAttribute("success", message);
                } else {
                    session.setAttribute("error", "Invalid Field(s)");
                }
                if (initQuestion.getStatus() == Question.QuestionStatus.APPROVED) {
                    return "redirect:/contributor/questions";
                }
            } else {
                session.setAttribute("error", "Invalid Field(s)");
            }
            return "redirect:/contributor/answers/" + initQuestion.getId();
        } else {
            session.setAttribute("error", "Invalid Field(s)");
            return "redirect:/contributor/questions";
        }
    }

    @PostMapping("option/{questionId}")
    public String saveOption(HttpSession session, Model model, @ModelAttribute Option newOption, @PathVariable("questionId") int initQuestionId) {
        int id = (Integer) (session.getAttribute("contributor") != null ? session.getAttribute("contributor") : -1);
        if (id <= 0 && !contributorRepo.existsById(id)) {
            session.setAttribute("error", "Login Required");
            return "redirect:/login";
        }
        Contributor contributor = contributorRepo.findById(id).get();
        if (questionRepo.existsById(initQuestionId)) {
            Question initQuestion = questionRepo.findById(initQuestionId).get();
            if (newOption.getParentAnswer() != null && answerRepo.existsById(newOption.getParentAnswer().getId())) {
                Answer initAnswer = answerRepo.findById(newOption.getParentAnswer().getId()).get();
                if (contributor.getType() != Contributor.ContributorType.SUPER
                        && !(initAnswer.getStatus() == Answer.AnswerStatus.CREATED
                        && initAnswer.getContributor() == contributor)) {
                    session.setAttribute("error", "Access Denied");
                    return "redirect:/contributor/questions";
                }
                Option savedOption = optionRepo.save(newOption);
                if (savedOption.getId() > 0) {
                    String message = savedOption.getId() == savedOption.getId()
                            ? "Answer Option Updated Successfully"
                            : "Answer Option Added Successfully";
                    session.setAttribute("success", message);
                } else {
                    session.setAttribute("error", "Invalid Field(s)");
                }
                if (initQuestion.getStatus() == Question.QuestionStatus.APPROVED) {
                    return "redirect:/contributor/questions";
                }

            } else {
                session.setAttribute("error", "Invalid Field(s)");
            }
            return "redirect:/contributor/answers/" + initQuestion.getId();
        } else {
            session.setAttribute("error", "Invalid Field(s)");
            return "redirect:/contributor/questions";
        }
    }

    @PostMapping("poll_item")
    public String savePollItem(HttpSession session, Model model, @ModelAttribute PollItem newPollItem) {
        int id = (Integer) (session.getAttribute("contributor") != null ? session.getAttribute("contributor") : -1);
        if (id <= 0 && !contributorRepo.existsById(id)) {
            session.setAttribute("error", "Login Required");
            return "redirect:/login";
        }
        Contributor contributor = contributorRepo.findById(id).get();
        if (contributor.getType() == Contributor.ContributorType.REGULAR) {
            session.setAttribute("error", "Access Denied");
            return "redirect:/contributor/dashboard";
        }
        if (newPollItem.getPoll() == null) {
            session.setAttribute("error", "Invalid Poll");
        } else if (newPollItem.getPoll().getStatus() != Poll.PollStatus.CREATED) {
            session.setAttribute("error", "Cannot add poll item to submitted poll");
        } else if (pollItemRepo.findAllByPoll(newPollItem.getPoll()).size() >= 5) {
            session.setAttribute("error", "Maximum Poll Item reached for this poll");
        } else if (pollItemRepo.findByPollAndName(newPollItem.getPoll(), newPollItem.getName()) != null) {
            session.setAttribute("error", "Poll Item with this name already exist in this poll");
        } else {
            PollItem savedPollItem = pollItemRepo.save(newPollItem);
            if (savedPollItem.getId() > 0) {
                String message = savedPollItem.getId() == savedPollItem.getId()
                        ? "Poll Item Updated Successfully"
                        : "Poll Item Added Successfully";
                session.setAttribute("success", message);
            } else {
                session.setAttribute("error", "Invalid Field(s)");
            }
        }
        return "redirect:/contributor/polls";
    }

    @Async
    private void sendBroadcastMessages(Broadcast broadcast) {
        List<Tag> tags = broadcast.getTags().stream().map(BroadcastTag::getTag).collect(Collectors.toList());
        List<User> users = findEligibleUsers(tags);

        for (User user : users) {
            Message message = new Message();
            message.setType(Message.MessageType.BROADCAST);
            message.setTime(new Date());
            message.setUser(user);
            message.setBroadcast(broadcast);
            message = messageRepo.save(message);
            message.getUser().setFeedbacks(new ArrayList<>());

            String jsonMessage = Constants.GSON_EXPOSE.toJson(message);
            simpMessagingTemplate.convertAndSendToUser(user.getPhone(), "/reply", jsonMessage);
        }
    }

    @Async
    private void sendPollMessages(Poll poll) {
        List<Tag> tags = poll.getTags().stream().map(PollTag::getTag).collect(Collectors.toList());
        List<User> users = findEligibleUsers(tags);

        for (User user : users) {
            Message message = new Message();
            message.setType(Message.MessageType.POLL);
            message.setTime(new Date());
            message.setUser(user);
            message.setPoll(poll);
            message = messageRepo.save(message);
            message.getUser().setFeedbacks(new ArrayList<>());

            String jsonMessage = Constants.GSON_EXPOSE.toJson(message);
            simpMessagingTemplate.convertAndSendToUser(user.getPhone(), "/reply", jsonMessage);
        }
    }

    private List<User> findEligibleUsers(List<Tag> tags) {
        List<User> eligibleUsers = new ArrayList<>();
        for (User user : userRepo.findAll()) {
            List<Tag> userTags = user.getTags().stream().map(UserTag::getTag).collect(Collectors.toList());
            if (tags.stream().filter(userTags::contains).count() > 0) {
                eligibleUsers.add(user);
            }
        }
        return eligibleUsers;
    }
}
