/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fut.chatbot.repo;

import com.fut.chatbot.model.Broadcast;
import com.fut.chatbot.model.Message;
import com.fut.chatbot.model.Poll;
import com.fut.chatbot.model.User;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author ahmad
 */
public interface MessageRepo extends CrudRepository<Message, Integer>{
 
    List<Message> findAllByPoll(Poll poll);
    
    List<Message> findAllByBroadcast(Broadcast broadcast);
    
    List<Message> findAllByUser(User user);
}
