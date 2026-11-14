package com.banking.sofware.design.fx_trading.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Entity(name = "Transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigDecimal id;

    @Column
    private String username;

    @Column
    private String primaryCcy;

    @Column
    private String secondaryCcy;

    @Column
    private BigDecimal rate;

    @Column
    private String action;

    @Column
    private BigDecimal notional;

    @Column
    private String tenor;

    @Column
    private Date date;
}