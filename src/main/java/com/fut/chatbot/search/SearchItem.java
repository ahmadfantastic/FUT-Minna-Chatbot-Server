/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fut.chatbot.search;

import com.fut.chatbot.model.Question;
import com.google.gson.annotations.Expose;

/**
 *
 * @author ahmad
 */
public class SearchItem {

    @Expose
    private double score;
    @Expose
    private Question question;

    public SearchItem(double score, Question question) {
        this.score = score;
        this.question = question;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

}
