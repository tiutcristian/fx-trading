package com.banking.sofware.design.fx_trading.mapper;

import com.banking.sofware.design.fx_trading.entity.Transaction;
import com.banking.sofware.design.fx_trading.response.TransactionResponse;
import com.banking.sofware.design.fx_trading.util.RateUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

@Mapper(imports = {RateUtil.class, Date.class})
public interface TransactionMapper {
    TransactionMapper INSTANCE = Mappers.getMapper( TransactionMapper.class );

    @Mapping(qualifiedByName = "convertRateForTransfer", target = "rate")
    @Mapping(qualifiedByName = "convertDateToLong", target = "date")
    TransactionResponse transactionToTransactionResponse(Transaction transaction);

    @Named("convertRateForTransfer")
    default BigDecimal convertRateForTransfer(BigDecimal rate) {
        if (rate == null) {
            return null;
        }
        return rate.divide(RateUtil.RATE_MULTIPLIER);
    }

    @Named("convertDateToLong")
    default Long convertDateToLong(Date date) {
        if (date == null) {
            return null;
        }
        return date.getTime();
    }

    @Mapping(qualifiedByName = "convertRateForPersisting", target = "rate", source = "rate")
    @Mapping(expression = "java(new Date())", target = "date")
    Transaction transactionResponseToTransaction(TransactionResponse transactionResponse, BigDecimal rate);


    @Named("convertRateForPersisting")
    default BigDecimal convertRateForPersisting(BigDecimal rate) {
        return rate.multiply(RateUtil.RATE_MULTIPLIER).setScale(0, RoundingMode.HALF_UP);
    }
}