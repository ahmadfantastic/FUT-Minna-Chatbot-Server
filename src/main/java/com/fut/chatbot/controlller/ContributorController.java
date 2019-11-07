/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fut.chatbot.controlller;

import com.fut.chatbot.model.*;
import com.fut.chatbot.repo.*;
import com.fut.chatbot.search.HibernateSearchService;
import com.fut.chatbot.search.SearchItem;
import com.fut.chatbot.util.Constants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author ahmad
 */
@Controller
@RequestMapping("contributor")
public class ContributorController {

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
    BroadcastRepo broadcastRepo;
    @Autowired
    PollRepo pollRepo;
    @Autowired
    private HibernateSearchService searchService;

    @GetMapping("")
    public String template(HttpSession session, Model model) {
        int id = (Integer) (session.getAttribute("contributor") != null ? session.getAttribute("contributor") : -1);
        if (id <= 0 && !contributorRepo.existsById(id)) {
            session.setAttribute("error", "Login Required");
            return "redirect:/login";
        }
        Contributor contributor = contributorRepo.findById(id).get();
        String page = (String) session.getAttribute("page");

        model.addAttribute("success", session.getAttribute("success"));
        model.addAttribute("message", session.getAttribute("message"));
        model.addAttribute("error", session.getAttribute("error"));

        model.addAttribute("contributor", contributor);
        model.addAttribute("IMG_BASE", Constants.IMAGE_LOCAL_BASE);
        model.addAttribute("page", page != null ? page : "dashboard");

        session.removeAttribute("success");
        session.removeAttribute("message");
        session.removeAttribute("error");

        return "contributor/template";
    }

    @RequestMapping("logout")
    public String logout(HttpSession session, Model model) {
        session.removeAttribute("contributor");
        session.setAttribute("message", "Logout Successful");
        return "redirect:/login";
    }

    @RequestMapping("dashboard")
    public String dashboard(HttpSession session, Model model, @RequestParam(defaultValue = "no") String embed) {
        int id = (Integer) (session.getAttribute("contributor") != null ? session.getAttribute("contributor") : -1);
        if (id <= 0 && !contributorRepo.existsById(id)) {
            session.setAttribute("error", "Login Required");
            return "redirect:/login";
        }
        session.setAttribute("page", "dashboard");
        if (!embed.equals("yes")) {
            return "redirect:/contributor";
        }
        Contributor contributor = contributorRepo.findById(id).get();
        model.addAttribute("me", contributor);
        return "contributor/dashboard";
    }

    @RequestMapping("questions")
    public String questions(HttpSession session, Model model, @RequestParam(defaultValue = "no") String embed) {
        int id = (Integer) (session.getAttribute("contributor") != null ? session.getAttribute("contributor") : -1);
        if (id <= 0 && !contributorRepo.existsById(id)) {
            session.setAttribute("error", "Login Required");
            return "redirect:/login";
        }
        session.setAttribute("page", "questions");
        if (!embed.equals("yes")) {
            return "redirect:/contributor";
        }
        Contributor contributor = contributorRepo.findById(id).get();
        List<Question> questions = (List<Question>) questionRepo.findAll();
        HashMap<Integer, Boolean> possibleConflicts = new HashMap<>();
        for (Question question : questions) {
            List<SearchItem> searchItems = searchService.search(question.getBody());
            boolean noConflicts = searchItems == null || searchItems.isEmpty() || searchItems.get(0).getScore() < 0.5;
            possibleConflicts.put(question.getId(), noConflicts);
        }
        model.addAttribute("me", contributor);
        model.addAttribute("IMG_BASE", Constants.IMAGE_LOCAL_BASE);
        model.addAttribute("questions", questions);
        model.addAttribute("conflictStatus", possibleConflicts);
        model.addAttribute("tags", tagRepo.findAll());
        return "contributor/questions";
    }

    @RequestMapping("broadcasts")
    public String broadcasts(HttpSession session, Model model, @RequestParam(defaultValue = "no") String embed) {
        int id = (Integer) (session.getAttribute("contributor") != null ? session.getAttribute("contributor") : -1);

        if (id <= 0 && !contributorRepo.existsById(id)) {
            session.setAttribute("error", "Login Required");
            return "redirect:/login";
        }
        Contributor contributor = contributorRepo.findById(id).get();
        if (contributor.getType() == Contributor.ContributorType.REGULAR) {
            session.setAttribute("error", "Access Denied");
            return "redirect:/login";
        }
        session.setAttribute("page", "broadcasts");
        if (!embed.equals("yes")) {
            return "redirect:/contributor";
        }
        List<Broadcast> broadcasts = (List<Broadcast>) broadcastRepo.findAll();
        model.addAttribute("me", contributor);
        model.addAttribute("IMG_BASE", Constants.IMAGE_LOCAL_BASE);
        model.addAttribute("broadcasts", broadcasts);
        model.addAttribute("tags", tagRepo.findAll());
        return "contributor/broadcasts";
    }

    @RequestMapping("polls")
    public String polls(HttpSession session, Model model, @RequestParam(defaultValue = "no") String embed) {
        int id = (Integer) (session.getAttribute("contributor") != null ? session.getAttribute("contributor") : -1);
        if (id <= 0 && !contributorRepo.existsById(id)) {
            session.setAttribute("error", "Login Required");
            return "redirect:/login";
        }
        Contributor contributor = contributorRepo.findById(id).get();
        if (contributor.getType() == Contributor.ContributorType.REGULAR) {
            session.setAttribute("error", "Access Denied");
            return "redirect:/login";
        }
        session.setAttribute("page", "polls");
        if (!embed.equals("yes")) {
            return "redirect:/contributor";
        }
        List<Poll> polls = (List<Poll>) pollRepo.findAll();
        List<String[]> voteData = new ArrayList<>();
        for (Poll poll : polls) {
            if (poll.getStatus() == Poll.PollStatus.PROGRESS || poll.getStatus() == Poll.PollStatus.FINISHED) {
                int count = poll.getItems().size();
                String[] labels = new String[count];
                int[] data = new int[count];
                for (int i = 0; i < count; i++) {
                    PollItem item = poll.getItems().get(i);
                    labels[i] = "\"" + item.getName() + "\"";
                    data[i] = item.getVotes().size();
                }
                voteData.add(new String[]{
                    String.valueOf(poll.getId()),
                    Arrays.toString(labels),
                    Arrays.toString(data),
                    String.valueOf(count)
                });
            }
        }

        model.addAttribute("me", contributor);
        model.addAttribute("IMG_BASE", Constants.IMAGE_LOCAL_BASE);
        model.addAttribute("polls", polls);
        model.addAttribute("voteData", voteData);
        model.addAttribute("tags", tagRepo.findAll());
        return "contributor/polls";
    }

    @RequestMapping("users")
    public String users(HttpSession session, Model model, @RequestParam(defaultValue = "no") String embed) {
        int id = (Integer) (session.getAttribute("contributor") != null ? session.getAttribute("contributor") : -1);
        if (id <= 0 && !contributorRepo.existsById(id)) {
            session.setAttribute("error", "Login Required");
            return "redirect:/login";
        }
        session.setAttribute("page", "users");
        if (!embed.equals("yes")) {
            return "redirect:/contributor";
        }
        Contributor contributor = contributorRepo.findById(id).get();
        model.addAttribute("me", contributor);
        model.addAttribute("users", userRepo.findAll());
        return "contributor/users";
    }

    @RequestMapping("contributors")
    public String contributors(HttpSession session, Model model, @RequestParam(defaultValue = "no") String embed) {
        int id = (Integer) (session.getAttribute("contributor") != null ? session.getAttribute("contributor") : -1);
        if (id <= 0 && !contributorRepo.existsById(id)) {
            session.setAttribute("error", "Login Required");
            return "redirect:/login";
        }
        session.setAttribute("page", "contributors");
        if (!embed.equals("yes")) {
            return "redirect:/contributor";
        }
        Contributor contributor = contributorRepo.findById(id).get();
        if (contributor.getType() != Contributor.ContributorType.SUPER) {
            session.setAttribute("error", "Access Denied");
            return "redirect:/contributor/dashboard";
        }
        model.addAttribute("me", contributor);
        model.addAttribute("IMG_BASE", Constants.IMAGE_LOCAL_BASE);
        model.addAttribute("contributors", contributorRepo.findAll());
        model.addAttribute("tags", tagRepo.findAll());
        return "contributor/contributors";
    }

    @RequestMapping("asks")
    public String asks(HttpSession session, Model model, @RequestParam(defaultValue = "no") String embed) {
        int id = (Integer) (session.getAttribute("contributor") != null ? session.getAttribute("contributor") : -1);
        if (id <= 0 && !contributorRepo.existsById(id)) {
            session.setAttribute("error", "Login Required");
            return "redirect:/login";
        }
        session.setAttribute("page", "asks");
        if (!embed.equals("yes")) {
            return "redirect:/contributor";
        }
        model.addAttribute("asks", askRepo.findAll());
        return "contributor/asks";
    }

    @RequestMapping("feedbacks")
    public String feedbacks(HttpSession session, Model model, @RequestParam(defaultValue = "no") String embed) {
        int id = (Integer) (session.getAttribute("contributor") != null ? session.getAttribute("contributor") : -1);
        if (id <= 0 && !contributorRepo.existsById(id)) {
            session.setAttribute("error", "Login Required");
            return "redirect:/login";
        }
        session.setAttribute("page", "feedbacks");
        if (!embed.equals("yes")) {
            return "redirect:/contributor";
        }
        Contributor contributor = contributorRepo.findById(id).get();
        model.addAttribute("me", contributor);
        model.addAttribute("feedbacks", feedbackRepo.findAll());
        return "contributor/feedbacks";
    }

    @RequestMapping("preferences")
    public String preferences(HttpSession session, Model model, @RequestParam(defaultValue = "no") String embed) {
        int id = (Integer) (session.getAttribute("contributor") != null ? session.getAttribute("contributor") : -1);
        if (id <= 0 && !contributorRepo.existsById(id)) {
            session.setAttribute("error", "Login Required");
            return "redirect:/login";
        }
        session.setAttribute("page", "preferences");
        if (!embed.equals("yes")) {
            return "redirect:/contributor";
        }
        Contributor contributor = contributorRepo.findById(id).get();
        model.addAttribute("me", contributor);
        model.addAttribute("preferences", preferenceRepo.findAll());
        return "contributor/preferences";
    }

    @RequestMapping("tags")
    public String tags(HttpSession session, Model model, @RequestParam(defaultValue = "no") String embed) {
        int id = (Integer) (session.getAttribute("contributor") != null ? session.getAttribute("contributor") : -1);
        if (id <= 0 && !contributorRepo.existsById(id)) {
            session.setAttribute("error", "Login Required");
            return "redirect:/login";
        }
        session.setAttribute("page", "tags");
        if (!embed.equals("yes")) {
            return "redirect:/contributor";
        }
        Contributor contributor = contributorRepo.findById(id).get();
        model.addAttribute("me", contributor);
        model.addAttribute("IMG_BASE", Constants.IMAGE_LOCAL_BASE);
        model.addAttribute("tags", tagRepo.findAll());
        return "contributor/tags";
    }

    @RequestMapping("answers/{id}")
    public String answers(HttpSession session, Model model, @RequestParam(defaultValue = "no") String embed, @PathVariable("id") int questionId) {
        int id = (Integer) (session.getAttribute("contributor") != null ? session.getAttribute("contributor") : -1);
        if (id <= 0 && !contributorRepo.existsById(id)) {
            session.setAttribute("error", "Login Required");
            return "redirect:/login";
        }
        session.setAttribute("page", "answers_" + questionId);
        if (!embed.equals("yes")) {
            return "redirect:/contributor";
        }
        Contributor contributor = contributorRepo.findById(id).get();
        if (questionRepo.existsById(questionId)) {
            Question question = questionRepo.findById(questionId).get();
            if (question.getStatus() == Question.QuestionStatus.APPROVED && question.getAnswer() == null) {
                model.addAttribute("me", contributor);
                model.addAttribute("question", question);
                model.addAttribute("answers", answerRepo.findAllByQuestionAndStatus(question, Answer.AnswerStatus.REQUESTED));
                model.addAttribute("mine", answerRepo.findByQuestionAndContributor(question, contributor));
                model.addAttribute("preferences", preferenceRepo.findAllByType(Preference.PreferenceType.PREDEFINED));
                model.addAttribute("IMG_BASE", Constants.IMAGE_LOCAL_BASE);
                return "contributor/answers";
            } else {
                session.setAttribute("error", "Not Applicable");
                return "redirect:/contributor/questions";
            }
        } else {
            session.setAttribute("error", "Invalid Field(s)");
            return "redirect:/contributor";
        }
    }

    @RequestMapping("answer/{id}")
    public String answer(HttpSession session, Model model, @RequestParam(defaultValue = "no") String embed, @PathVariable("id") int questionId) {
        int id = (Integer) (session.getAttribute("contributor") != null ? session.getAttribute("contributor") : -1);
        if (id <= 0 && !contributorRepo.existsById(id)) {
            session.setAttribute("error", "Login Required");
            return "redirect:/login";
        }
        Contributor contributor = contributorRepo.findById(id).get();
        if (questionRepo.existsById(questionId)) {
            Question question = questionRepo.findById(questionId).get();
            if (question.getStatus() == Question.QuestionStatus.APPROVED && question.getAnswer() != null) {
                model.addAttribute("me", contributor);
                model.addAttribute("question", question);
                model.addAttribute("IMG_BASE", Constants.IMAGE_LOCAL_BASE);
                return "contributor/approved_answer";
            } else {
                session.setAttribute("error", "Not Applicable");
                return "redirect:/contributor/questions";
            }
        } else {
            session.setAttribute("error", "Invalid Field(s)");
            return "redirect:/contributor";
        }
    }

    @RequestMapping("answer/edit/{id}")
    public String answerEdit(HttpSession session, Model model, @RequestParam(defaultValue = "no") String embed, @PathVariable("id") int questionId) {
        int id = (Integer) (session.getAttribute("contributor") != null ? session.getAttribute("contributor") : -1);
        if (id <= 0 && !contributorRepo.existsById(id)) {
            session.setAttribute("error", "Login Required");
            return "redirect:/login";
        }
        Contributor contributor = contributorRepo.findById(id).get();
        if (contributor.getType() != Contributor.ContributorType.SUPER) {
            session.setAttribute("error", "Access Denied");
            return "redirect:/contributor/questions";
        } else if (questionRepo.existsById(questionId)) {
            Question question = questionRepo.findById(questionId).get();
            if (question.getStatus() == Question.QuestionStatus.APPROVED && question.getAnswer() != null) {
                model.addAttribute("me", contributor);
                model.addAttribute("question", question);
                model.addAttribute("IMG_BASE", Constants.IMAGE_LOCAL_BASE);
                return "contributor/approved_answer_edit";
            } else {
                session.setAttribute("error", "Not Applicable");
                return "redirect:/contributor/questions";
            }
        } else {
            session.setAttribute("error", "Invalid Field(s)");
            return "redirect:/contributor";
        }
    }

    @RequestMapping("conflicts/{id}")
    public String conflicts(HttpSession session, Model model, @RequestParam(defaultValue = "no") String embed, @PathVariable("id") int questionId) {
        int id = (Integer) (session.getAttribute("contributor") != null ? session.getAttribute("contributor") : -1);
        if (id <= 0 && !contributorRepo.existsById(id)) {
            session.setAttribute("error", "Login Required");
            return "redirect:/login";
        }
        session.setAttribute("page", "conflicts_" + questionId);
        if (!embed.equals("yes")) {
            return "redirect:/contributor";
        }
        Contributor contributor = contributorRepo.findById(id).get();
        if (questionRepo.existsById(questionId)) {
            Question question = questionRepo.findById(questionId).get();
            model.addAttribute("me", contributor);
            model.addAttribute("question", question);
            model.addAttribute("searchItems", searchService.search(question.getBody()));
            model.addAttribute("IMG_BASE", Constants.IMAGE_LOCAL_BASE);
            return "contributor/conflicts";
        } else {
            session.setAttribute("error", "Invalid Field(s)");
            return "redirect:/contributor";
        }
    }

}
