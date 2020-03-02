package com.swp493.ivb.common.track;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.WhereJoinTable;

import com.swp493.ivb.common.artist.EntityArtist;
import com.swp493.ivb.common.release.EntityRelease;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "track")
//@SecondaryTable(name = "object_genre",
//    pkJoinColumns = @PrimaryKeyJoinColumn(referencedColumnName = "track_id"))
//@SecondaryTable(name = "user_object",
//    pkJoinColumns = @PrimaryKeyJoinColumn(referencedColumnName = "track_id"))
@NoArgsConstructor
@Getter
@Setter
public class EntityTrack {

    @Id
    private String id;

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
    private int mp3Offset;

    @NotBlank
    private String status;

//    private String genre;

    private String producer;

    @NotBlank
    @Column(name = "mp3_128")
    private String mp3128;

    @NotBlank
    @Column(name = "mp3_320")
    private String mp3320;

    // @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_object", 
            joinColumns = @JoinColumn(name = "track_id"), 
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    @WhereJoinTable(clause = "action='own'")
    private List<EntityArtist> artists;

    // @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "release_id")
    private EntityRelease release;
}
