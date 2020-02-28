package com.swp493.ivb.features.common.track;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "track")
//@SecondaryTable(name = "object_genre",
//    pkJoinColumns = @PrimaryKeyJoinColumn(referencedColumnName = "track_id"))
//@SecondaryTable(name = "user_object",
//    pkJoinColumns = @PrimaryKeyJoinColumn(referencedColumnName = "track_id"))
public class TrackEntity {

    @Id
    private String id;

    private String releaseId;

    @NotBlank
    private String title;

    @NotBlank
    @Column(name = "duration_128")
    private int duration128;

    @NotBlank
    @Column(name = "duration_320")
    private int duration320;

    @NotBlank
    @Column(name = "file_size_128")
    private int fileSize128;

    @NotBlank
    @Column(name = "file_size_320")
    private int fileSize320;

    @NotBlank
    @Column(name = "mp3_offset")
    private int mp3offset;

    @NotBlank
    @Column(name = "track_state")
    private String state;

//    private String genre;

    private String producer;

    @NotBlank
    @Column(name = "mp3_128")
    @JsonIgnore
    private String mp3128;

    @NotBlank
    @Column(name = "mp3_320")
    @JsonIgnore
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
     * @return the duration128
     */
    public int getDuration128() {
        return duration128;
    }

    /**
     * @param duration128 the duration128 to set
     */
    public void setDuration128(int duration128) {
        this.duration128 = duration128;
    }

    /**
     * @return the duration320
     */
    public int getDuration320() {
        return duration320;
    }

    /**
     * @param duration320 the duration320 to set
     */
    public void setDuration320(int duration320) {
        this.duration320 = duration320;
    }

    /**
     * @return the fileSize128
     */
    public int getFileSize128() {
        return fileSize128;
    }

    /**
     * @param fileSize128 the fileSize128 to set
     */
    public void setFileSize128(int fileSize128) {
        this.fileSize128 = fileSize128;
    }

    /**
     * @return the fileSize320
     */
    public int getFileSize320() {
        return fileSize320;
    }

    /**
     * @param fileSize320 the fileSize320 to set
     */
    public void setFileSize320(int fileSize320) {
        this.fileSize320 = fileSize320;
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

//    /**
//     * @return the genre
//     */
//    public String getGenre() {
//        return genre;
//    }
//
//    /**
//     * @param genre the genre to set
//     */
//    public void setGenre(String genre) {
//        this.genre = genre;
//    }

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
