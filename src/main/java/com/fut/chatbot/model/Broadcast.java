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
@Table(name = "broadcast")
@XmlRootElement
public class Broadcast {

    public enum BroadcastStatus {
        CREATED, REQUESTED, APPROVED
    };

    public enum BroadcastExpiry {
        HOUR, HOUR_2, DAY, WEEK
    };

    @Id
    @Expose
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Expose
    private String body;

    @Expose
    @OneToMany(mappedBy = "broadcast", cascade = CascadeType.REMOVE)
    private List<BroadcastTag> tags;

    @JoinColumn(name = "contributor", referencedColumnName = "id")
    @ManyToOne
    @Expose
    private Contributor contributor;

    @Expose
    private BroadcastStatus status;

    @Expose
    private BroadcastExpiry expiry;

    @Expose
    private Date setupTime;

    public Broadcast() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<BroadcastTag> getTags() {
        return tags;
    }

    public void setTags(List<BroadcastTag> tags) {
        this.tags = tags;
    }

    public Contributor getContributor() {
        return contributor;
    }

    public void setContributor(Contributor contributor) {
        this.contributor = contributor;
    }

    public BroadcastStatus getStatus() {
        return status;
    }

    public void setStatus(BroadcastStatus status) {
        this.status = status;
    }

    public BroadcastExpiry getExpiry() {
        return expiry;
    }

    public void setExpiry(BroadcastExpiry expiry) {
        this.expiry = expiry;
    }

    public Date getSetupTime() {
        return setupTime;
    }

    public void setSetupTime(Date setupTime) {
        this.setupTime = setupTime;
    }

    @Ignore
    public String toJSON() {
        return Constants.GSON_EXPOSE.toJson(this);
    }
}
