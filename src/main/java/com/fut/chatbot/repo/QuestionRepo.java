/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fut.chatbot.repo;

import com.fut.chatbot.model.Question;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author ahmad
 */
public interface QuestionRepo extends CrudRepository<Question, Integer> {

}
