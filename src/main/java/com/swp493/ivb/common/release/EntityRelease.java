package com.swp493.ivb.common.release;

import java.sql.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.WhereJoinTable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.swp493.ivb.common.artist.EntityArtist;
import com.swp493.ivb.common.track.EntityTrack;

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
    @GenericGenerator(name = "indie-id", strategy = "com.swp493.ivb.util.IndieIdentifierGenerator")
    @GeneratedValue(generator = "indie-id")
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

    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    @OneToOne(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_object", 
            joinColumns = @JoinColumn(name = "release_id"), 
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    @WhereJoinTable(clause = "action='own'")
    private EntityArtist artist;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "release")
    private List<EntityTrack> tracks;
}
