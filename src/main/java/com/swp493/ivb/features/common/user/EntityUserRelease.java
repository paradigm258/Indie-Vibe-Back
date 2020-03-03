package com.swp493.ivb.features.common.user;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

import com.swp493.ivb.common.release.EntityRelease;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_object")
@NoArgsConstructor
@Getter
@Setter
public class EntityUserRelease implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "release_id")
    private EntityRelease release;

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Id
    private String action;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof EntityUserRelease)) return false;
        
        EntityUserRelease that = (EntityUserRelease) obj;
        return Objects.equals(release.getId(), that.release.getId()) &&
                Objects.equals(user.getId(), that.user.getId()) &&
                Objects.equals(action, that.action);
    }

    @Override
    public int hashCode() {
        return Objects.hash(release.getId(), user.getId(), action);
    }
}
