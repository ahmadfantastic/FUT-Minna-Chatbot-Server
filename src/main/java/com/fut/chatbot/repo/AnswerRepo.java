/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fut.chatbot.repo;

import com.fut.chatbot.model.Answer;
import com.fut.chatbot.model.Contributor;
import com.fut.chatbot.model.Question;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author ahmad
 */
public interface AnswerRepo extends CrudRepository<Answer, Integer>{
    
    List<Answer> findAllByQuestion(Question question);
    
    List<Answer> findAllByQuestionAndStatus(Question question, Answer.AnswerStatus status);
    
    Answer findByQuestionAndContributor(Question question, Contributor contributor);
}
