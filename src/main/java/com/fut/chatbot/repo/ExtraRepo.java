/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fut.chatbot.repo;

import com.fut.chatbot.model.Answer;
import com.fut.chatbot.model.Extra;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author ahmad
 */
public interface ExtraRepo extends CrudRepository<Extra, Integer>{
    
    List<Extra> findByAnswer(Answer answer);
}
