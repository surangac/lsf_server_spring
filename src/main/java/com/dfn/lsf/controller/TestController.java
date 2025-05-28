package com.dfn.lsf.controller;

import com.dfn.lsf.model.CashAcc;
import com.dfn.lsf.util.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.http.HttpResponse;
import java.util.List;

@RestController
public class TestController {

    @Autowired
    private Helper helper;

    // This is a placeholder for the TestController class.
    // You can add methods here to handle specific requests or perform tests.

    // Example method (uncomment and modify as needed):

    @GetMapping("/test")
    public ResponseEntity<List<CashAcc>> testEndpoint() {
        List<CashAcc> cashAccounts = helper.getLsfTypeCashAccountForApp("11140210", "16840");
        return ResponseEntity.ok(cashAccounts);
    }
}
