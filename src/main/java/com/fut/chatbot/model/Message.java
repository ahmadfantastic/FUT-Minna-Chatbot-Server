/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fut.chatbot.model;

import com.google.gson.annotations.Expose;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Ahmadfantastic
 */
@Entity
@Table(name = "message")
@XmlRootElement
public class Message {
    
    public enum MessageType{ANSWER, POLL, BROADCAST}

    @Id
    @Expose
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JoinColumn(name = "answer", referencedColumnName = "id")
    @ManyToOne
    @Expose
    private Answer answer;
    
    @JoinColumn(name = "poll", referencedColumnName = "id")
    @ManyToOne
    @Expose
    private Poll poll;
    
    @JoinColumn(name = "broadcast", referencedColumnName = "id")
    @ManyToOne
    @Expose
    private Broadcast broadcast;

    @JoinColumn(name = "user", referencedColumnName = "id")
    @ManyToOne
    @Expose
    private User user;
    
    @Expose
    private MessageType type;

    @Expose
    private Date time;

    public Message() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Poll getPoll() {
        return poll;
    }

    public void setPoll(Poll poll) {
        this.poll = poll;
    }

    public Broadcast getBroadcast() {
        return broadcast;
    }

    public void setBroadcast(Broadcast broadcast) {
        this.broadcast = broadcast;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }
    
    
}
