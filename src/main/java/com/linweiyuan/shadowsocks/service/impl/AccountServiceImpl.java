package com.linweiyuan.shadowsocks.service.impl;

import com.linweiyuan.commons.model.ApiCode;
import com.linweiyuan.commons.model.R;
import com.linweiyuan.shadowsocks.common.Constant;
import com.linweiyuan.shadowsocks.entity.Account;
import com.linweiyuan.shadowsocks.repository.AccountRepository;
import com.linweiyuan.shadowsocks.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

@SuppressWarnings("ConstantConditions")
@Slf4j
@Service
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public R ping(int id) {
        log.info("check account status -> " + id);
        R.RBuilder builder = R.builder();
        Socket socket = new Socket();
        Account account = accountRepository.getOne(id);
        if (account == null) {
            builder = builder.code(ApiCode.ERR.getValue()).msg("账号不存在 -> " + id);
        }
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(account.getIp(), Integer.parseInt(account.getPort())), Constant.PING_TIMEOUT);
            account.setStatus(Constant.ACCOUNT_STATUS_ENABLE);
            builder = builder.code(ApiCode.OK.getValue()).msg("可用 -> " + account.getIp() + ":" + account.getPort());
        } catch (IOException e) {
            account.setStatus(Constant.ACCOUNT_STATUS_DISABLE);
            builder = builder.code(ApiCode.ERR.getValue()).msg("被墙 -> " + account.getIp() + ":" + account.getPort());
        }
        accountRepository.save(account);
        return builder.build();
    }

    @Override
    public R findAccounts(int page, int limit, String keyword) {
        log.info("get accounts by keyword -> " + keyword);
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("id").descending());
        Page<Account> p;
        if (StringUtils.isEmpty(keyword)) {
            p = accountRepository.findAll(pageable);
        } else {
            keyword += "%";
            p = accountRepository.findByIpLikeOrPortLikeOrPasswordLikeOrMethodLikeOrLocationLikeOrConfigLikeOrStatusLike(
                    keyword, keyword, keyword, keyword, keyword, keyword, keyword, pageable);
        }
        return R.builder().data(p.getContent()).count(p.getTotalElements()).build();
    }
}
