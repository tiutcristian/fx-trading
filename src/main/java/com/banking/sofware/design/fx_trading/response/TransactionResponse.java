package com.banking.sofware.design.fx_trading.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransactionResponse {

    private BigDecimal id;

    private String username;

    private String primaryCcy;

    private String secondaryCcy;

    private BigDecimal rate;

    private String action;

    private BigDecimal notional;

    private String tenor;

    private Long date;
}