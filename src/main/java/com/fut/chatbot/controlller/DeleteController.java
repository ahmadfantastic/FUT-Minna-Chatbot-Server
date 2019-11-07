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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author ahmad
 */
@Controller
@RequestMapping("contributor/delete")
public class DeleteController {

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

    @RequestMapping("preference/{id}")
    public String deletePreference(HttpSession session, Model model, @PathVariable("id") int preferenceId) {
        int id = (Integer) (session.getAttribute("contributor") != null ? session.getAttribute("contributor") : -1);
        if (id <= 0 && !contributorRepo.existsById(id)) {
            session.setAttribute("error", "Login Required");
            return "redirect:/login";
        }
        if (preferenceRepo.existsById(preferenceId)) {
            Preference preference = preferenceRepo.findById(preferenceId).get();
            preference.getValues().forEach((value) -> {
                preferenceValueRepo.delete(value);
            });
            preferenceRepo.delete(preference);
            session.setAttribute("success", "Preference Deleted Successfully");
        } else {
            session.setAttribute("error", "Invalid Field(s)");
        }
        return "redirect:/contributor/preferences";
    }

    @RequestMapping("preference_value/{id}")
    public String deletePreferenceValue(HttpSession session, Model model, @PathVariable("id") int preferencevalueId) {
        int id = (Integer) (session.getAttribute("contributor") != null ? session.getAttribute("contributor") : -1);
        if (id <= 0 && !contributorRepo.existsById(id)) {
            session.setAttribute("error", "Login Required");
            return "redirect:/login";
        }
        if (preferenceValueRepo.existsById(preferencevalueId)) {
            PreferenceValue preferencevalue = preferenceValueRepo.findById(preferencevalueId).get();
            preferenceValueRepo.delete(preferencevalue);
            session.setAttribute("success", "Preference Value Deleted Successfully");
        } else {
            session.setAttribute("error", "Invalid Field(s)");
        }
        return "redirect:/contributor/preferences";
    }

    @RequestMapping("tag/{id}")
    public String deleteTag(HttpSession session, Model model, @PathVariable("id") int tagId) {
        int id = (Integer) (session.getAttribute("contributor") != null ? session.getAttribute("contributor") : -1);
        if (id <= 0 && !contributorRepo.existsById(id)) {
            session.setAttribute("error", "Login Required");
            return "redirect:/login";
        }
        if (tagRepo.existsById(tagId)) {
            Tag tag = tagRepo.findById(tagId).get();
            tag.getQuestions().forEach((questionTag) -> {
                questionTagRepo.delete(questionTag);
            });
            tagRepo.delete(tag);
            session.setAttribute("success", "Tag Deleted Successfully");
        } else {
            session.setAttribute("error", "Invalid Field(s)");
        }
        return "redirect:/contributor/tags";
    }

    @RequestMapping("question/{id}")
    public String deleteQuestion(HttpSession session, Model model, @PathVariable("id") int questionId) {
        int id = (Integer) (session.getAttribute("contributor") != null ? session.getAttribute("contributor") : -1);
        if (id <= 0 && !contributorRepo.existsById(id)) {
            session.setAttribute("error", "Login Required");
            return "redirect:/login";
        }
        if (questionRepo.existsById(questionId)) {
            Question question = questionRepo.findById(questionId).get();
            question.getTags().forEach((questionTag) -> {
                questionTagRepo.delete(questionTag);
            });
            questionRepo.delete(question);
            session.setAttribute("success", "Question Deleted Successfully");
        } else {
            session.setAttribute("error", "Invalid Field(s)");
        }
        return "redirect:/contributor/questions";
    }

    @RequestMapping("broadcast/{id}")
    public String deleteBroadcast(HttpSession session, Model model, @PathVariable("id") int broadcastId) {
        int id = (Integer) (session.getAttribute("contributor") != null ? session.getAttribute("contributor") : -1);
        if (id <= 0 && !contributorRepo.existsById(id)) {
            session.setAttribute("error", "Login Required");
            return "redirect:/login";
        }
        if (broadcastRepo.existsById(broadcastId)) {
            Broadcast broadcast = broadcastRepo.findById(broadcastId).get();
            broadcast.getTags().forEach((broadcastTag) -> {
                broadcastTagRepo.delete(broadcastTag);
            });
            broadcastRepo.delete(broadcast);
            session.setAttribute("success", "Broadcast Deleted Successfully");
        } else {
            session.setAttribute("error", "Invalid Field(s)");
        }
        return "redirect:/contributor/broadcasts";
    }

    @RequestMapping("poll/{id}")
    public String deletePoll(HttpSession session, Model model, @PathVariable("id") int pollId) {
        int id = (Integer) (session.getAttribute("contributor") != null ? session.getAttribute("contributor") : -1);
        if (id <= 0 && !contributorRepo.existsById(id)) {
            session.setAttribute("error", "Login Required");
            return "redirect:/login";
        }
        if (pollRepo.existsById(pollId)) {
            Poll poll = pollRepo.findById(pollId).get();
            poll.getTags().forEach((pollTag) -> {
                pollTagRepo.delete(pollTag);
            });
            pollRepo.delete(poll);
            session.setAttribute("success", "Poll Deleted Successfully");
        } else {
            session.setAttribute("error", "Invalid Field(s)");
        }
        return "redirect:/contributor/polls";
    }

    @RequestMapping("answer/{id}")
    public String deleteAnswer(HttpSession session, Model model, @PathVariable("id") int answerId) {
        int id = (Integer) (session.getAttribute("contributor") != null ? session.getAttribute("contributor") : -1);
        if (id <= 0 && !contributorRepo.existsById(id)) {
            session.setAttribute("error", "Login Required");
            return "redirect:/login";
        }
        if (answerRepo.existsById(answerId)) {
            Answer answer = answerRepo.findById(answerId).get();
            answerRepo.delete(answer);
            session.setAttribute("success", "Answer Deleted Successfully");
        } else {
            session.setAttribute("error", "Invalid Field(s)");
        }
        return "redirect:/contributor";
    }

    @RequestMapping("question_tag/{id}")
    public String deleteQuestionTag(HttpSession session, Model model, @PathVariable("id") int questionTagId) {
        int id = (Integer) (session.getAttribute("contributor") != null ? session.getAttribute("contributor") : -1);
        if (id <= 0 && !contributorRepo.existsById(id)) {
            session.setAttribute("error", "Login Required");
            return "redirect:/login";
        }
        if (questionTagRepo.existsById(questionTagId)) {
            QuestionTag questiontag = questionTagRepo.findById(questionTagId).get();
            questionTagRepo.delete(questiontag);
            session.setAttribute("success", "Tag Removed from Question Successfully");
        } else {
            session.setAttribute("error", "Invalid Field(s)");
        }
        return "redirect:/contributor/questions";
    }

    @RequestMapping("broadcast_tag/{id}")
    public String deleteBroadcastTag(HttpSession session, Model model, @PathVariable("id") int broadcastTagId) {
        int id = (Integer) (session.getAttribute("contributor") != null ? session.getAttribute("contributor") : -1);
        if (id <= 0 && !contributorRepo.existsById(id)) {
            session.setAttribute("error", "Login Required");
            return "redirect:/login";
        }
        if (broadcastTagRepo.existsById(broadcastTagId)) {
            BroadcastTag broadcasttag = broadcastTagRepo.findById(broadcastTagId).get();
            broadcastTagRepo.delete(broadcasttag);
            session.setAttribute("success", "Tag Removed from Broadcast Successfully");
        } else {
            session.setAttribute("error", "Invalid Field(s)");
        }
        return "redirect:/contributor/broadcasts";
    }

    @RequestMapping("poll_tag/{id}")
    public String deletePollTag(HttpSession session, Model model, @PathVariable("id") int pollTagId) {
        int id = (Integer) (session.getAttribute("contributor") != null ? session.getAttribute("contributor") : -1);
        if (id <= 0 && !contributorRepo.existsById(id)) {
            session.setAttribute("error", "Login Required");
            return "redirect:/login";
        }
        if (pollTagRepo.existsById(pollTagId)) {
            PollTag polltag = pollTagRepo.findById(pollTagId).get();
            pollTagRepo.delete(polltag);
            session.setAttribute("success", "Tag Removed from Poll Successfully");
        } else {
            session.setAttribute("error", "Invalid Field(s)");
        }
        return "redirect:/contributor/polls";
    }

    @RequestMapping("contributor_tag/{id}")
    public String deleteContributorTag(HttpSession session, Model model, @PathVariable("id") int contributorTagId) {
        int id = (Integer) (session.getAttribute("contributor") != null ? session.getAttribute("contributor") : -1);
        if (id <= 0 && !contributorRepo.existsById(id)) {
            session.setAttribute("error", "Login Required");
            return "redirect:/login";
        }
        Contributor contributor = contributorRepo.findById(id).get();
        if (contributor.getType() != Contributor.ContributorType.SUPER) {
            session.setAttribute("error", "Access Denied");
        } else if (contributorTagRepo.existsById(contributorTagId)) {
            ContributorTag contributortag = contributorTagRepo.findById(contributorTagId).get();
            contributorTagRepo.delete(contributortag);
            session.setAttribute("success", "Tag Removed from Contributor Successfully");
        } else {
            session.setAttribute("error", "Invalid Field(s)");
        }
        return "redirect:/contributor/contributors";
    }

    @RequestMapping("extra/{id}")
    public String deleteExtra(HttpSession session, Model model, @PathVariable("id") int extraId) {
        int id = (Integer) (session.getAttribute("contributor") != null ? session.getAttribute("contributor") : -1);
        if (id <= 0 && !contributorRepo.existsById(id)) {
            session.setAttribute("error", "Login Required");
            return "redirect:/login";
        }
        if (extraRepo.existsById(extraId)) {
            Extra extra = extraRepo.findById(extraId).get();
            extraRepo.delete(extra);
            session.setAttribute("success", "Extra Removed Successfully");
        } else {
            session.setAttribute("error", "Invalid Field(s)");
        }
        return "redirect:/contributor/";
    }

    @RequestMapping("poll_item/{id}")
    public String deletePollItem(HttpSession session, Model model, @PathVariable("id") int pollItemId) {
        int id = (Integer) (session.getAttribute("contributor") != null ? session.getAttribute("contributor") : -1);
        if (id <= 0 && !contributorRepo.existsById(id)) {
            session.setAttribute("error", "Login Required");
            return "redirect:/login";
        }
        if (pollItemRepo.existsById(pollItemId)) {
            PollItem pollItem = pollItemRepo.findById(pollItemId).get();
            if (pollItem.getPoll().getStatus() != Poll.PollStatus.CREATED) {
                session.setAttribute("error", "Cannot Delete a Poll Item of sent Poll");
            } else {
                pollItemRepo.delete(pollItem);
                session.setAttribute("success", "Poll Item Removed Successfully");
            }
        } else {
            session.setAttribute("error", "Invalid Field(s)");
        }
        return "redirect:/contributor/";
    }

    @RequestMapping("option/{id}")
    public String deleteOption(HttpSession session, Model model, @PathVariable("id") int optionId) {
        int id = (Integer) (session.getAttribute("contributor") != null ? session.getAttribute("contributor") : -1);
        if (id <= 0 && !contributorRepo.existsById(id)) {
            session.setAttribute("error", "Login Required");
            return "redirect:/login";
        }
        if (optionRepo.existsById(optionId)) {
            Option option = optionRepo.findById(optionId).get();
            Answer answer = option.getChildAnswer();
            if (answer != null) {
                answerRepo.findById(answer.getId()).get().getOptions().forEach((option1) -> {
                    optionRepo.delete(option1);
                });
                answerRepo.delete(answer);
            }
            optionRepo.delete(option);
            session.setAttribute("success", "Option Removed Successfully");
        } else {
            session.setAttribute("error", "Invalid Field(s)");
        }
        return "redirect:/contributor/";
    }

}
