/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fut.chatbot.repo;

import com.fut.chatbot.model.Tag;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author ahmad
 */
public interface TagRepo extends CrudRepository<Tag, Integer>{
    
    Tag findByName(String name);
}
