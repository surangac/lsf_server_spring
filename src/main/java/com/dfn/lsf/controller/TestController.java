package com.dfn.lsf.controller;

import com.dfn.lsf.model.CashAcc;
import com.dfn.lsf.service.impl.InvestorAccountCreationProcessor;
import com.dfn.lsf.service.scheduler.SettlementCalculationProcessor;
import com.dfn.lsf.util.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TestController {

    @Autowired
    private Helper helper;

    @Autowired
    private InvestorAccountCreationProcessor investorAccountCreationProcessor;

    @Autowired
    private SettlementCalculationProcessor settlementCalculationProcessor;

    // This is a placeholder for the TestController class.
    // You can add methods here to handle specific requests or perform tests.

    // Example method (uncomment and modify as needed):

    @GetMapping("/test")
    public ResponseEntity<List<CashAcc>> testEndpoint() {
        List<CashAcc> cashAccounts = helper.getLsfTypeCashAccounts("11140210", "16840");
        return ResponseEntity.ok(cashAccounts);
    }

    @GetMapping("/createExchangeAccount")
    public ResponseEntity<String> createExcahngeAccountManually(@RequestParam String appId, @RequestParam String tradingAccId) {
        String response = investorAccountCreationProcessor.manualCreationExchangeAccount(appId, tradingAccId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/calculateSettlement")
    public ResponseEntity<String> calculateSettlement() {
        settlementCalculationProcessor.runSettlementCalculation();
        return ResponseEntity.ok("Done, settlement calculation initiated.");
    }
}
