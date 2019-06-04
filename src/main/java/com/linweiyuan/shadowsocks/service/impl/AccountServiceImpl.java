package com.linweiyuan.shadowsocks.service.impl;

import com.linweiyuan.commons.model.ApiCode;
import com.linweiyuan.commons.model.R;
import com.linweiyuan.commons.util.JsonUtil;
import com.linweiyuan.shadowsocks.common.Constant;
import com.linweiyuan.shadowsocks.entity.Account;
import com.linweiyuan.shadowsocks.repository.AccountRepository;
import com.linweiyuan.shadowsocks.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

@SuppressWarnings("ConstantConditions")
@Slf4j
@Service
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final StringRedisTemplate redis;

    public AccountServiceImpl(AccountRepository accountRepository, StringRedisTemplate redis) {
        this.accountRepository = accountRepository;
        this.redis = redis;
    }

    @Override
    public R findAll() {
        log.info("get accounts");
        ValueOperations<String, String> ops = redis.opsForValue();
        List<Account> accounts;
        String key = Constant.REDIS_KEY_SHADOWSOCKS_ACCOUNTS;
        if (redis.hasKey(key)) {
            accounts = JsonUtil.toList(ops.get(key), Account.class);
        } else {
            accounts = accountRepository.findAll();
            ops.set(key, JsonUtil.toJson(accounts));
        }
        return R.builder().data(accounts).build();
    }

    @Override
    public R findByPage(int page) {
        if (page <= 0) {
            page = 1;
        }
        log.info("get accounts by page -> " + page);
        Pageable pageable = PageRequest.of(page - 1, Constant.PAGE_SIZE, Sort.by("id"));
        Page<Account> p = accountRepository.findAll(pageable);
        return R.builder().data(p).build();
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
        try {
            socket.connect(new InetSocketAddress(account.getIp(), Integer.parseInt(account.getPort())), Constant.PING_TIMEOUT);
            account.setStatus(Constant.ACCOUNT_STATUS_ENABLE);
            builder = builder.code(ApiCode.ERR.getValue()).msg("可用 -> " + account.getIp() + ":" + account.getPort());
        } catch (IOException e) {
            account.setStatus(Constant.ACCOUNT_STATUS_DISABLE);
            builder = builder.code(ApiCode.ERR.getValue()).msg("被墙 -> " + account.getIp() + ":" + account.getPort());
        }
        accountRepository.save(account);
        return builder.build();
    }

    @Override
    public R findByParam(int page, String param) {
        log.info("get accounts by param -> " + param);
        Pageable pageable = PageRequest.of(page - 1, Constant.PAGE_SIZE, Sort.by("id"));
        Page<Account> p;
        if (StringUtils.isEmpty(param)) {
            p = accountRepository.findAll(pageable);
        } else {
            param += "%";
            p = accountRepository.findByIpLikeOrPortLikeOrPasswordLikeOrMethodLikeOrLocationLikeOrConfigLikeOrStatusLike(
                    param, param, param, param, param, param, param, pageable);
        }
        return R.builder().data(p).build();
    }
}
