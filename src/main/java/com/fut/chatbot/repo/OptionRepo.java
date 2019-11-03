/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fut.chatbot.repo;

import com.fut.chatbot.model.Answer;
import com.fut.chatbot.model.Option;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author ahmad
 */
public interface OptionRepo extends CrudRepository<Option, Integer>{
    
    List<Option> findByParentAnswer(Answer parentAnswer);
}