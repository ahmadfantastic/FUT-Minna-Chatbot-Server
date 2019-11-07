/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fut.chatbot.model;

import com.google.gson.annotations.Expose;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 *
 * @author ahmad
 */
@Entity
@Table(name = "user")
@XmlRootElement
public class User {

    @Id
    @Expose
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Expose
    @Column(length = 50)
    private String name;

    @Expose
    @Column(length = 5)
    private String code;

    @Expose
    @Column(unique = true, length = 11)
    private String phone;

    @Expose
    @Fetch(value = FetchMode.SUBSELECT)
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private List<UserPreference> preferences;

    @Expose
    @Fetch(value = FetchMode.SUBSELECT)
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private List<UserTag> tags;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Feedback> feedbacks;

    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<UserPreference> getPreferences() {
        return preferences;
    }

    public List<UserTag> getTags() {
        return tags;
    }

    public void setTags(List<UserTag> tags) {
        this.tags = tags;
    }

    public void setPreferences(List<UserPreference> preferences) {
        this.preferences = preferences;
    }

    public List<Feedback> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(List<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + this.id;
        hash = 67 * hash + Objects.hashCode(this.phone);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User other = (User) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.phone, other.phone)) {
            return false;
        }
        return true;
    }

    
}
