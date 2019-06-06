package com.linweiyuan.shadowsocks.service;

import com.linweiyuan.commons.model.R;

import java.io.IOException;

public interface AccountService {
    R ping(int id);

    R findAccounts(int page, int limit, String keyword);

    R sync(String jsessionid) throws IOException;
}
