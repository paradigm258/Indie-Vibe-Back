package com.swp493.ivb.common.mdata;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.Where;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "master_data")
@Where(clause = "type = 'genre'")
@NoArgsConstructor
@Getter
@Setter
public class EntityGenre {

    @Id
    @NotBlank
    private String id;

    @NotBlank
    private String name;

    private String thumbnail;

    private String description;
}
