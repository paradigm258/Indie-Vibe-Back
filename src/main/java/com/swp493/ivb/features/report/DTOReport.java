package com.swp493.ivb.features.report;

import java.util.Date;

import com.swp493.ivb.common.artist.DTOArtistFull;
import com.swp493.ivb.common.mdata.DTOReportType;
import com.swp493.ivb.common.user.DTOUserPublic;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DTOReport {
    private String id;
    private DTOReportType type;
    private DTOUserPublic reporter;
    private DTOArtistFull artist;
    private String reason;
    private String status;
    private Date date;
}