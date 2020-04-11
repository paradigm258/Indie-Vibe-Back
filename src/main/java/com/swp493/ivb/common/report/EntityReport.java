package com.swp493.ivb.common.report;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.swp493.ivb.common.artist.EntityArtist;
import com.swp493.ivb.common.mdata.EntityMasterData;
import com.swp493.ivb.common.user.EntityUser;

import org.hibernate.annotations.GenericGenerator;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "report")
@Getter
@Setter
public class EntityReport {
    
    @Id
    @GenericGenerator(name = "id", strategy = "com.swp493.ivb.util.IndieIdentifierGenerator")
    @GeneratedValue(generator = "id")
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    private EntityMasterData type;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id")
    private EntityUser reporter;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id")
    private EntityArtist artist;

    private String reason;
    
    private String status;

    private Date date;
}