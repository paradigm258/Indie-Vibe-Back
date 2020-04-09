package com.swp493.ivb.features.workspace;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "artist_stats")
@Entity
public class EntityArtistStats {

    @Id
    @GenericGenerator(name = "id", strategy = "com.swp493.ivb.util.IndieIdentifierGenerator")
    @GeneratedValue(generator = "id")
    private String id;

    private String objectId;

    private String type;

    private long count;

    private Date recordMonth;

}