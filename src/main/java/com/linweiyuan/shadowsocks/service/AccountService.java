package com.linweiyuan.shadowsocks.service;

import com.linweiyuan.commons.model.X;

import java.io.IOException;

public interface AccountService {
    X ping(int id);

    X findAccounts(int page, int limit, String keyword);

    X sync(String jsessionid) throws IOException;

    X download(int id) throws IOException;
}
