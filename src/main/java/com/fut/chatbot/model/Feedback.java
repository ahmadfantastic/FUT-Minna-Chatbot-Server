/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fut.chatbot.model;

import javax.persistence.Column;
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
@Table(name = "feedback")
@XmlRootElement
public class Feedback {

    public enum FeedbackType {
        GOOD, OUTDATED, INCORRECT, INCOMPLETE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JoinColumn(name = "answer", referencedColumnName = "id")
    @ManyToOne
    private Answer answer;

    @JoinColumn(name = "user", referencedColumnName = "id")
    @ManyToOne
    private User user;

    @Column(length = 255)
    private String message;

    private FeedbackType type;

    public Feedback() {
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public FeedbackType getType() {
        return type;
    }

    public void setType(FeedbackType type) {
        this.type = type;
    }

}
