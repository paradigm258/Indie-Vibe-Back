package com.swp493.ivb.common.relationship;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.swp493.ivb.common.playlist.EntityPlaylist;
import com.swp493.ivb.common.user.EntityUser;

import org.hibernate.annotations.GenericGenerator;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * EntityUserPlaylist
 */
@Entity
@Table(name = "user_object")
@NoArgsConstructor
@Getter
@Setter
public class EntityUserPlaylist {

    @Id
    @GenericGenerator(name = "id", strategy = "com.swp493.ivb.util.IndieIdentifierGenerator")
    @GeneratedValue(generator = "id")
    String id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "playlist_id")
    private EntityPlaylist playlist;

    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private EntityUser user;

    private String action;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof EntityUserPlaylist)) return false;
        
        EntityUserPlaylist that = (EntityUserPlaylist) obj;
        return Objects.equals(playlist.getId(), that.playlist.getId()) &&
                Objects.equals(user.getId(), that.user.getId()) &&
                Objects.equals(action, that.action);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playlist.getId(), user.getId(), action);
    }
}