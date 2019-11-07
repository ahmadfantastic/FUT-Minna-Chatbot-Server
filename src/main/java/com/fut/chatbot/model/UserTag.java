/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fut.chatbot.model;

import com.fut.chatbot.util.Constants;
import com.google.gson.annotations.Expose;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import jdk.nashorn.internal.ir.annotations.Ignore;

/**
 *
 * @author Ahmadfantastic
 */
@Entity
@Table(name = "user_tag")
@XmlRootElement
public class UserTag {

    @Id
    @Expose
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @JoinColumn(name = "user", referencedColumnName = "id")
    @ManyToOne
    private User user;
    
    @JoinColumn(name = "tag", referencedColumnName = "id")
    @ManyToOne
    @Expose
    private Tag tag;

    public UserTag() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    @Ignore
    public String toJSON(){
        return Constants.GSON_EXPOSE.toJson(this);
    }

}
