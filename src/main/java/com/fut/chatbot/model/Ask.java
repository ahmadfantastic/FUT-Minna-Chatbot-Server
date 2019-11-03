/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fut.chatbot.model;

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
@Table(name = "ask")
@XmlRootElement
public class Ask {

    public enum AskAccuracy {
        UNKNOWN, RIGHT, WRONG
    };

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String text;

    @JoinColumn(name = "question", referencedColumnName = "id")
    @ManyToOne
    private Question question;

    @JoinColumn(name = "user", referencedColumnName = "id")
    @ManyToOne
    private User user;

    private AskAccuracy accuracy;

    public Ask() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public AskAccuracy getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(AskAccuracy accuracy) {
        this.accuracy = accuracy;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
