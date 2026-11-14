package com.banking.sofware.design.fx_trading.service;

import com.banking.sofware.design.fx_trading.dto.QuoteResponse;
import com.banking.sofware.design.fx_trading.entity.Transaction;
import com.banking.sofware.design.fx_trading.mapper.TransactionMapper;
import com.banking.sofware.design.fx_trading.repository.FxTradingRepository;
import com.banking.sofware.design.fx_trading.response.TransactionResponse;
import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FxTradingService {

    @Autowired
    private FxTradingRepository repository;

    @SuppressWarnings("unchecked")
    public List<TransactionResponse> getTransactions() {
        return repository.findAll().stream().map(TransactionMapper.INSTANCE::transactionToTransactionResponse).collect(Collectors.toList());
    }

    @Autowired
    private QuoteProxyService proxyRatesService;


    @Transactional
    public void makeTransaction(TransactionResponse transactionDto) {
        // Important: in a real application validations should be made - here for example
        String action = transactionDto.getAction();
        if (StringUtils.isBlank(action) || !Arrays.asList("BUY", "SELL").contains(action.toUpperCase())) {
            throw new IllegalArgumentException("Action not supported!");
        }

        QuoteResponse ratePair = getCurrentRate(transactionDto.getPrimaryCcy(), transactionDto.getSecondaryCcy());
        BigDecimal rate = "BUY".equalsIgnoreCase(action) ? ratePair.getBuyRate() : ratePair.getSellRate();

        Transaction transaction = TransactionMapper.INSTANCE.transactionResponseToTransaction(transactionDto,rate);
        repository.save(transaction);
    }

    private QuoteResponse getCurrentRate(String primaryCcy, String secondaryCcy) {
        try {
            return proxyRatesService.getRate(primaryCcy, secondaryCcy);
        } catch (Exception e) {
            log.error("Could not obtain response from quote service!", e);
            throw e;
        }
    }

}