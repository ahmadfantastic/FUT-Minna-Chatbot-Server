/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fut.chatbot.repo;

import com.fut.chatbot.model.User;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author ahmad
 */
public interface UserRepo extends CrudRepository<User, Integer>{
    
    User findByPhone(String phone);
    
    boolean existsByPhone(String phone);
}
