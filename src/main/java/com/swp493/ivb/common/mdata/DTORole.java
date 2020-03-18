package com.swp493.ivb.common.mdata;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class DTORole {

    private String id;

    private String name;

    private String type = "role";
}
