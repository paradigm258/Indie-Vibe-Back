package com.swp493.indievibe.Features.Authentication.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "User")
public class IndieUser{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String email;
    @Column(name = "fbId")
    private String fbId;
    private String role;
    private String password;

    public IndieUser() {}

    public IndieUser(String email,String fbId, String role,String password) {
        this.email = email;
        this.fbId = fbId;
        this.role = role;
        this.password = password;
    }
    /**
     * @return the id
     */
    public long getId() {
        return id;
    }
    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }
    /**
     * @return the fbId
     */
    public String getFbId() {
        return fbId;
    }
    /**
     * @return the role
     */
    public String getRole() {
        return role;
    }
    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }
    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }
    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }
    /**
     * @param fbId the fbId to set
     */
    public void setFbId(String fbId) {
        this.fbId = fbId;
    }
    /**
     * @param role the role to set
     */
    public void setRole(String role) {
        this.role = role;
    }
    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
}