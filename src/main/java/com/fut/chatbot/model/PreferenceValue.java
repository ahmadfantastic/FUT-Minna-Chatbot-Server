/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fut.chatbot.model;

import com.fut.chatbot.util.Constants;
import com.google.gson.annotations.Expose;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import jdk.nashorn.internal.ir.annotations.Ignore;

/**
 *
 * @author Ahmadfantastic
 */
@Entity
@Table(name = "preference_value")
@XmlRootElement
public class PreferenceValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Expose
    private int id;
    
    @JoinColumn(name = "preference", referencedColumnName = "id")
    @ManyToOne
    @Expose
    private Preference preference;
    
    @Expose
    @Column(length = 50)
    private String value;

    public PreferenceValue() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Preference getPreference() {
        return preference;
    }

    public void setPreference(Preference preference) {
        this.preference = preference;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Ignore
    public String toJSON(){
        return Constants.GSON_EXPOSE.toJson(this);
    }
}
