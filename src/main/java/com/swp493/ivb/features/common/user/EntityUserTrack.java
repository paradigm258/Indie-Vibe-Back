package com.swp493.ivb.features.common.user;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.swp493.ivb.common.track.EntityTrack;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_object")
@NoArgsConstructor
@Getter
@Setter
public class EntityUserTrack implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "track_id")
    private EntityTrack track;

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Id
    private String action;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof EntityUserTrack)) return false;
        
        EntityUserTrack that = (EntityUserTrack) obj;
        return Objects.equals(track.getId(), that.track.getId()) &&
                Objects.equals(user.getId(), that.user.getId()) &&
                Objects.equals(action, that.action);
    }

    @Override
    public int hashCode() {
        return Objects.hash(track.getId(), user.getId(), action);
    }
}
