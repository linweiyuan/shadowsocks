package com.linweiyuan.shadowsocks.controller;

import com.linweiyuan.commons.model.R;
import com.linweiyuan.shadowsocks.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

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

    @ResponseBody
    @GetMapping("/sync/{jsessionid}")
    public R sync(@PathVariable String jsessionid) throws IOException {
        return accountService.sync(jsessionid);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity download(@PathVariable int id) throws IOException {
        return (ResponseEntity) accountService.download(id).getData();
    }
}
