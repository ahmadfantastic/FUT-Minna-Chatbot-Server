/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fut.chatbot.model;

import com.fut.chatbot.util.Constants;
import com.google.gson.annotations.Expose;
import java.util.Date;
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
@Table(name = "poll")
@XmlRootElement
public class Poll {

    public enum PollStatus {
        CREATED, REQUESTED, PROGRESS, FINISHED
    };

    public enum PollExpiry {
        DAY, DAY_3, WEEK,
    };

    @Id
    @Expose
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Expose
    @Column(name="title", unique = true, length = 30)
    private String title;

    @Expose
    @Column(length = 150)
    private String body;

    @Expose
    @OneToMany(mappedBy = "poll", cascade = CascadeType.REMOVE)
    private List<PollTag> tags;

    @OneToMany(mappedBy = "poll", cascade = CascadeType.REMOVE)
    private List<PollItem> items;

    @JoinColumn(name = "contributor", referencedColumnName = "id")
    @ManyToOne
    @Expose
    private Contributor contributor;

    @Expose
    private PollStatus status;

    @Expose
    private PollExpiry expiry;

    @Expose
    private Date setupTime;

    public Poll() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<PollTag> getTags() {
        return tags;
    }

    public void setTags(List<PollTag> tags) {
        this.tags = tags;
    }

    public Contributor getContributor() {
        return contributor;
    }

    public void setContributor(Contributor contributor) {
        this.contributor = contributor;
    }

    public PollStatus getStatus() {
        return status;
    }

    public void setStatus(PollStatus status) {
        this.status = status;
    }

    public PollExpiry getExpiry() {
        return expiry;
    }

    public void setExpiry(PollExpiry expiry) {
        this.expiry = expiry;
    }

    public Date getSetupTime() {
        return setupTime;
    }

    public void setSetupTime(Date setupTime) {
        this.setupTime = setupTime;
    }

    public List<PollItem> getItems() {
        return items;
    }

    public void setItems(List<PollItem> items) {
        this.items = items;
    }
    
    @Ignore
    public int getTotalVotes(){
        int total = 0;
        total = items.stream().map((item) -> item.getVotes().size()).reduce(total, Integer::sum);
        return total;
    }

    @Ignore
    public String toJSON() {
        return Constants.GSON_EXPOSE.toJson(this);
    }
}
