package com.indievibe.Authentication.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class IndieUser{
    private String email;
    @Id
    private long id;
    private String role;
    private String password;

    public IndieUser() {

    }

    public IndieUser(String email,long id, String role,String password) {
        this.email = email;
        this.id = id;
        this.role = role;
        this.password = password;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }
    /**
     * @return the id
     */
    public long getId() {
        return id;
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
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }
    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
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