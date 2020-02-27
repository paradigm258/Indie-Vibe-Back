package com.swp493.ivb.features.common.track;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import com.swp493.ivb.features.common.user.UserEntity;

@Entity
@Table(name = "track")
@SecondaryTable(name = "object_genre",
    pkJoinColumns = @PrimaryKeyJoinColumn(referencedColumnName = "track_id"))
@SecondaryTable(name = "user_object",
    pkJoinColumns = @PrimaryKeyJoinColumn(referencedColumnName = "track_id"))
public class TrackEntity {

    @Id
    private String id;

    private String releaseId;

    private UserEntity owner;

    @NotBlank
    private String title;

    @NotBlank
    private int duration;

    @NotBlank
    private int fileSize;

    @NotBlank
    private int mp3offset;

    @NotBlank
    private String state;

    private String genre;
    
    private String producer;

    @NotBlank
    private String mp3128;
    
    @NotBlank
    private String mp3320;

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the releaseId
     */
    public String getReleaseId() {
        return releaseId;
    }

    /**
     * @param releaseId the releaseId to set
     */
    public void setReleaseId(String releaseId) {
        this.releaseId = releaseId;
    }

    /**
     * @return the owner
     */
    public UserEntity getOwner() {
        return owner;
    }

    /**
     * @param owner the owner to set
     */
    public void setOwner(UserEntity owner) {
        this.owner = owner;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the duration
     */
    public int getDuration() {
        return duration;
    }

    /**
     * @param duration the duration to set
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * @return the fileSize
     */
    public int getFileSize() {
        return fileSize;
    }

    /**
     * @param fileSize the fileSize to set
     */
    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    /**
     * @return the mp3offset
     */
    public int getMp3offset() {
        return mp3offset;
    }

    /**
     * @param mp3offset the mp3offset to set
     */
    public void setMp3offset(int mp3offset) {
        this.mp3offset = mp3offset;
    }

    /**
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return the genre
     */
    public String getGenre() {
        return genre;
    }

    /**
     * @param genre the genre to set
     */
    public void setGenre(String genre) {
        this.genre = genre;
    }

    /**
     * @return the producer
     */
    public String getProducer() {
        return producer;
    }

    /**
     * @param producer the producer to set
     */
    public void setProducer(String producer) {
        this.producer = producer;
    }

    /**
     * @return the mp3128
     */
    public String getMp3128() {
        return mp3128;
    }

    /**
     * @param mp3128 the mp3128 to set
     */
    public void setMp3128(String mp3128) {
        this.mp3128 = mp3128;
    }

    /**
     * @return the mp3320
     */
    public String getMp3320() {
        return mp3320;
    }

    /**
     * @param mp3320 the mp3320 to set
     */
    public void setMp3320(String mp3320) {
        this.mp3320 = mp3320;
    }

}
