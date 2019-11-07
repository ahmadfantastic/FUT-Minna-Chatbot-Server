/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fut.chatbot.model;

import com.fut.chatbot.util.Constants;
import com.google.gson.annotations.Expose;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import jdk.nashorn.internal.ir.annotations.Ignore;

/**
 *
 * @author Ahmadfantastic
 */
@Entity
@Table(name = "poll_item")
@XmlRootElement
public class PollItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Expose
    private int id;

    @JoinColumn(name = "poll", referencedColumnName = "id")
    @ManyToOne
    @Expose
    private Poll poll;

    @Expose
    @OneToMany(mappedBy = "pollItem", cascade = CascadeType.REMOVE)
    private List<Vote> votes;

    @Expose
    @Column(length = 15)
    private String name;

    public PollItem() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Poll getPoll() {
        return poll;
    }

    public void setPoll(Poll poll) {
        this.poll = poll;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Vote> getVotes() {
        return votes;
    }

    public void setVotes(List<Vote> votes) {
        this.votes = votes;
    }

    @Ignore
    public String toJSON() {
        return Constants.GSON_EXPOSE.toJson(this);
    }
}
