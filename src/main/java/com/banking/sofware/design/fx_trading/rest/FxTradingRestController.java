package com.banking.sofware.design.fx_trading.rest;

import com.banking.sofware.design.fx_trading.response.TransactionResponse;
import com.banking.sofware.design.fx_trading.service.FxTradingService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transaction")
public class FxTradingRestController {

    @Autowired
    private FxTradingService tradingService;

    @CrossOrigin
    @GetMapping(produces = "application/json")
    public List<TransactionResponse> getTransactions(HttpServletResponse response) {
        try {
            return tradingService.getTransactions();
        } catch (Exception e) {
            response.setStatus(500);
            return null;
        }
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public void makeTransaction(@RequestBody TransactionResponse transaction, HttpServletResponse response) {
        try {
            tradingService.makeTransaction(transaction);
        } catch (Exception e) {
            response.setStatus(500);
        }
    }

}