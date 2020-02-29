package com.swp493.ivb.common.release;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.WhereJoinTable;

import com.swp493.ivb.common.artist.EntityArtist;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "audio_release")
@NoArgsConstructor
@Getter
@Setter
public class EntityRelease {

    @Id
    @NotBlank
    private String id;

    @NotBlank
    private String title;

    @NotBlank
    private String thumbnail;

    @NotBlank
    @Column(name = "released_date")
    private Date date;

    @NotBlank
    private String status;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_object", 
            joinColumns = @JoinColumn(name = "release_id"), 
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    @WhereJoinTable(clause = "action='own'")
    private EntityArtist artist;
}
