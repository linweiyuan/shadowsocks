package com.linweiyuan.shadowsocks.controller;

import com.linweiyuan.commons.model.R;
import com.linweiyuan.shadowsocks.service.AccountService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/accounts")
    public R accounts() {
        return accountService.findAll();
    }
}
