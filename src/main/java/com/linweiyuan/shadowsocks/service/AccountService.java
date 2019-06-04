package com.linweiyuan.shadowsocks.service;

import com.linweiyuan.commons.model.R;

public interface AccountService {
    R findAll();

    R findByPage(int page);

    R ping(int id);

    R findByParam(int page, String param);
}
