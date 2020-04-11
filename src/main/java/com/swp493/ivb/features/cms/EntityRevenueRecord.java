package com.swp493.ivb.features.cms;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.swp493.ivb.common.mdata.EntityMasterData;

import org.hibernate.annotations.GenericGenerator;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "revenue_record")
public class EntityRevenueRecord {
    @Id
    @GenericGenerator(name = "id", strategy = "com.swp493.ivb.util.IndieIdentifierGenerator")
    @GeneratedValue(generator = "id")
    private String id;

    long amount;
    Date recordedMonth;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "premium_type")
    EntityMasterData preminumType;
}