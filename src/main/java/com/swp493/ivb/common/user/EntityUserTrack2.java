package com.swp493.ivb.common.user;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.swp493.ivb.common.track.EntityTrack;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_object")
@NoArgsConstructor
@Getter
@Setter
public class EntityUserTrack2 {

    @Id
    @GenericGenerator(name = "id", strategy = "com.swp493.ivb.util.IndieIdentifierGenerator")
    @GeneratedValue(generator = "id")
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private EntityUser user;

    @ManyToOne
    @JoinColumn(name = "track_id")
    private EntityTrack track;

    private String action;
}
