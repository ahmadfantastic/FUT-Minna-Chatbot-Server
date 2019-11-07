/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fut.chatbot.repo;

import com.fut.chatbot.model.Poll;
import com.fut.chatbot.model.PollItem;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author ahmad
 */
public interface PollItemRepo extends CrudRepository<PollItem, Integer>{
    
    List<PollItem> findAllByPoll(Poll poll);
    
    PollItem findByPollAndName(Poll poll, String name);
}
