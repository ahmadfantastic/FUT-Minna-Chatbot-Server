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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import jdk.nashorn.internal.ir.annotations.Ignore;

/**
 *
 * @author Ahmadfantastic
 */
@Entity
@Table(name = "preference")
@XmlRootElement
public class Preference {

    public enum PreferenceType {
        PREDEFINED, USER_DEFINED
    }

    @Id
    @Expose
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Expose
    private PreferenceType type;

    @Expose
    private String name;

    @OneToMany(mappedBy = "preference", cascade = CascadeType.REMOVE)
    private List<PreferenceValue> values;

    public Preference() {
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

    public PreferenceType getType() {
        return type;
    }

    public void setType(PreferenceType type) {
        this.type = type;
    }

    public List<PreferenceValue> getValues() {
        return values;
    }

    public void setValues(List<PreferenceValue> values) {
        this.values = values;
    }
    
    @Ignore
    public String toJSON(){
        return Constants.GSON_EXPOSE.toJson(this);
    }
}
