/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fut.chatbot.model;

import com.google.gson.annotations.Expose;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 *
 * @author Ahmadfantastic
 */
@Entity
@Table(name = "user_preference")
@XmlRootElement
public class UserPreference {

    @Id
    @Expose
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JoinColumn(name = "user", referencedColumnName = "id")
    @ManyToOne
    private User user;

    @JoinColumn(name = "preference_value", referencedColumnName = "id")
    @ManyToOne
    @OnDelete(action=OnDeleteAction.CASCADE)
    private PreferenceValue preferenceValue;

    public UserPreference() {
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

    public PreferenceValue getPreference() {
        return preferenceValue;
    }

    public void setPreference(PreferenceValue preferenceValue) {
        this.preferenceValue = preferenceValue;
    }


}
