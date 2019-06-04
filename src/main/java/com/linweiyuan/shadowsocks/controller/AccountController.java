package com.linweiyuan.shadowsocks.controller;

import com.linweiyuan.commons.model.R;
import com.linweiyuan.shadowsocks.service.AccountService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
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

    @ResponseBody
    @GetMapping("/accounts/{page}")
    public R page(@PathVariable int page) {
        return accountService.findByPage(page);
    }
}
