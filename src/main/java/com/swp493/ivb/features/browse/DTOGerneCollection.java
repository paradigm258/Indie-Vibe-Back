package com.swp493.ivb.features.browse;

import java.util.List;

import com.swp493.ivb.common.mdata.DTOGenre;

import lombok.Getter;
import lombok.Setter;

/**
 * DTOGerneCollection
 */
@Getter
@Setter
public class DTOGerneCollection<T> {
    private DTOGenre genre;
    private List<T> items;
    
}