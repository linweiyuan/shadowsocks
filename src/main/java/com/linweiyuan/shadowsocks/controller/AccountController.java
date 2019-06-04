package com.linweiyuan.shadowsocks.controller;

import com.linweiyuan.commons.model.R;
import com.linweiyuan.shadowsocks.service.AccountService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    public R accounts() {
        return accountService.findAll();
    }

    @ResponseBody
    @GetMapping("/accounts/{page}")
    public R page(@PathVariable int page) {
        return accountService.findByPage(page);
    }

    @GetMapping
    public String index(Model model, @RequestParam(required = false, defaultValue = "1") int page) {
        model.addAttribute("r", accountService.findByPage(page));
        return "shadowsocks";
    }
}
