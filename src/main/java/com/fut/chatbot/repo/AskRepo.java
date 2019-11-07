/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fut.chatbot.repo;

import com.fut.chatbot.model.Ask;
import com.fut.chatbot.model.Ask.AskAccuracy;
import com.fut.chatbot.model.Question;
import com.fut.chatbot.model.QuestionTag;
import com.fut.chatbot.model.User;
import java.util.Date;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author ahmad
 */
public interface AskRepo extends CrudRepository<Ask, Integer>{
    
    long countByQuestionAndAccuracy(Question question, AskAccuracy accuracy);
    
    long countByQuestion(Question question);
    
    @Query("SELECT COUNT(a) FROM Ask a LEFT JOIN Question q ON a.question = q "
            + "LEFT JOIN QuestionTag qt ON qt.question = qt WHERE "
            + "a.askTime < :checkTime AND a.user = :user AND "
            + "a.question = :question AND qt = :qt")
    long countAskForSubscription(@Param("user") User user, @Param("checkTime") Date checkTime,
            @Param("question") Question question, @Param("qt") QuestionTag qt);
}
