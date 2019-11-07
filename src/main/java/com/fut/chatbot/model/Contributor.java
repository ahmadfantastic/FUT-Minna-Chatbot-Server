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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import jdk.nashorn.internal.ir.annotations.Ignore;

/**
 *
 * @author Ahmadfantastic
 */
@Entity
@Table(name = "contributor")
@XmlRootElement
public class Contributor {

    public enum ContributorStatus {
        ACTIVE, BLOCKED
    };

    public enum ContributorType {
        REGULAR, CLASS_REP, LECTURER, ASSOCIATION, SUPER
    };

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Expose
    private int id;

    @Expose
    @Column(length = 50)
    private String firstName;

    @Expose
    @Column(length = 50)
    private String lastName;

    @Expose
    @Column(length = 100)
    private String imageUrl;

    @Expose
    @Column(length = 50)
    private String email;

    @Expose
    @Column(length = 50)
    private String password;

    @Expose
    private Date registrationDate;

    @Expose
    private ContributorType type;

    @Expose
    private ContributorStatus status;
            
    @Expose
    @OneToMany(mappedBy = "contributor", cascade = CascadeType.REMOVE)
    private List<ContributorTag> tags;

    public Contributor() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public ContributorType getType() {
        return type;
    }

    public void setType(ContributorType type) {
        this.type = type;
    }

    public ContributorStatus getStatus() {
        return status;
    }

    public void setStatus(ContributorStatus status) {
        this.status = status;
    }

    public List<ContributorTag> getTags() {
        return tags;
    }

    public void setTags(List<ContributorTag> tags) {
        this.tags = tags;
    }

    @Ignore
    public String toJSON() {
        return Constants.GSON_EXPOSE.toJson(this);
    }
}
