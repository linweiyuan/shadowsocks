package com.linweiyuan.shadowsocks.service.impl;

import com.linweiyuan.commons.model.R;
import com.linweiyuan.commons.util.JsonUtil;
import com.linweiyuan.shadowsocks.common.Constant;
import com.linweiyuan.shadowsocks.entity.Account;
import com.linweiyuan.shadowsocks.repository.AccountRepository;
import com.linweiyuan.shadowsocks.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

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
}
