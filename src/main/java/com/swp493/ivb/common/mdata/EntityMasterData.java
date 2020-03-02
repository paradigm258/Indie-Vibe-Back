package com.swp493.ivb.common.mdata;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "master_data")
@NoArgsConstructor
@Getter
@Setter
public class EntityMasterData {

    @Id
    @NotBlank
    private String id;

    @NotBlank
    private String name;

    @NotBlank
    private String type;

    @NotBlank
    private String description;

    @NotBlank
    private String thumbnail;
}
