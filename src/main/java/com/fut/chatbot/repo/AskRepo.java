/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fut.chatbot.repo;

import com.fut.chatbot.model.Ask;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author ahmad
 */
public interface AskRepo extends CrudRepository<Ask, Integer>{
    
}
