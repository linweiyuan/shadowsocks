package com.linweiyuan.shadowsocks.service;

import com.linweiyuan.commons.model.R;

public interface AccountService {
    R ping(int id);

    R findAccounts(int page, int limit, String keyword);
}
