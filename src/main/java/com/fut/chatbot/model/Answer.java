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
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 *
 * @author Ahmadfantastic
 */
@Entity
@Table(name = "answer", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"contributor", "question"})
})
@XmlRootElement
public class Answer {

    public enum AnswerStatus {
        CREATED, REQUESTED
    };

    @Id
    @Expose
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Expose
    private String body;

    @Expose
    @Fetch(value = FetchMode.SUBSELECT)
    @OneToMany(mappedBy = "parentAnswer", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private List<Option> options;

    @Expose
    @Fetch(value = FetchMode.SUBSELECT)
    @OneToMany(mappedBy = "answer", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private List<Extra> extras;

    @Expose
    @JoinColumn(name = "contributor", referencedColumnName = "id")
    @ManyToOne
    private Contributor contributor;

    @Expose
    @JoinColumn(name = "approvedBy", referencedColumnName = "id")
    @ManyToOne
    private Contributor approvedBy;

    @JoinColumn(name = "question", referencedColumnName = "id")
    @ManyToOne
    private Question question;

    @JoinColumn(name = "option", referencedColumnName = "id")
    @OneToOne
    private Option option;

    @Expose
    @JoinColumn(name = "preference", referencedColumnName = "id")
    @OneToOne
    private Preference preference;

    @Expose
    private AnswerStatus status;

    public Answer() {
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

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public List<Extra> getExtras() {
        return extras;
    }

    public void setExtras(List<Extra> extras) {
        this.extras = extras;
    }

    public Contributor getContributor() {
        return contributor;
    }

    public void setContributor(Contributor contributor) {
        this.contributor = contributor;
    }

    public Contributor getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(Contributor approvedBy) {
        this.approvedBy = approvedBy;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Option getOption() {
        return option;
    }

    public void setOption(Option option) {
        this.option = option;
    }

    public Preference getPreference() {
        return preference;
    }

    public void setPreference(Preference preference) {
        this.preference = preference;
    }

    public AnswerStatus getStatus() {
        return status;
    }

    public void setStatus(AnswerStatus status) {
        this.status = status;
    }

    @Ignore
    public String toJSON() {
        return Constants.GSON_EXPOSE.toJson(this);
    }
}
