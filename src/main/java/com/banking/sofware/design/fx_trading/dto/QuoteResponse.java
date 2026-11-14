package com.banking.sofware.design.fx_trading.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuoteResponse {

    private BigDecimal buyRate;
    private BigDecimal sellRate;

    public QuoteResponse() {

    }

    public QuoteResponse(BigDecimal buyRate, BigDecimal sellRate) {
        this.buyRate = buyRate;
        this.sellRate = sellRate;
    }
}