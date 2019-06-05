package com.linweiyuan.shadowsocks.controller;

import com.linweiyuan.commons.model.R;
import com.linweiyuan.shadowsocks.service.AccountService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @ResponseBody
    @GetMapping("/accounts")
    public R accounts(@RequestParam int page, @RequestParam int limit, @RequestParam(defaultValue = "") String keyword) {
        return accountService.findAccounts(page, limit, keyword);
    }

    @ResponseBody
    @GetMapping("/ping/{id}")
    public R ping(@PathVariable int id) {
        return accountService.ping(id);
    }

    @GetMapping("/")
    public String index() {
        return "page/index.html";
    }
}
