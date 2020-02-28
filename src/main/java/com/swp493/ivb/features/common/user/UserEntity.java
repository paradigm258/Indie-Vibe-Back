package com.swp493.ivb.features.common.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "user")
public class UserEntity{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String displayName;

    @NotBlank
    @Column(name = "role_id")
    private String role;

    private String fbId;

    public UserEntity() {}

    /**
     * @return the id
     */
    public String getId() {
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
    public void setId(String id) {
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

    /**
     * @return the displayName
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @param displayName the displayName to set
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}