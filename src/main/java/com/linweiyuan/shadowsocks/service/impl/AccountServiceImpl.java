package com.linweiyuan.shadowsocks.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.linweiyuan.commons.model.ApiCode;
import com.linweiyuan.commons.model.R;
import com.linweiyuan.commons.util.HttpUtil;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"ConstantConditions"})
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

    @Override
    public R sync(String jsessionid) throws IOException {
        log.info("sync new accounts -> " + jsessionid);
        String json = HttpUtil.connect(Constant.API_SSR_TOOL)
                .proxy(Constant.HTTP_PROXY_SERVER, Constant.HTTP_PROXY_PORT)
                .cookie("JSESSIONID", jsessionid)
                .get()
                .text();
        if (json.equals("{}")) {
            return R.builder().msg("同步失败，jsessionid失效").build();
        }

        List<Account> latestAccounts = new ArrayList<>();
        JsonUtil.toObject(json, JSONObject.class)
                .getJSONArray("data")
                .forEach(array -> {
                    JSONObject obj = (JSONObject) array;
                    Account account = Account.builder()
                            .ip(obj.getString("server"))
                            .port(String.valueOf(obj.getIntValue("server_port")))
                            .password(obj.getString("password"))
                            .method(obj.getString("method"))
                            .location(obj.getString("country"))
                            .config(obj.getString("sslink").replace("SSRTOOL_Node%3A", ""))
                            .status(obj.getBooleanValue("m_station_cn_status") ? Constant.ACCOUNT_STATUS_ENABLE : Constant.ACCOUNT_STATUS_DISABLE)
                            .build();
                    latestAccounts.add(account);
                });

        List<Account> newAccounts = new ArrayList<>();
        List<Account> dbAccounts = accountRepository.findAll();
        for (Account latestAccount : latestAccounts) {
            boolean exists = false;
            for (Account dbAccount : dbAccounts) {
                if (dbAccount.getConfig().equals(latestAccount.getConfig())) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                newAccounts.add(latestAccount);
            }
        }
        accountRepository.saveAll(newAccounts);
        return R.builder().msg("同步完成，新增" + newAccounts.size() + "个账号").build();
    }

    @Override
    public R download(int id) throws IOException {
        log.info("download config -> " + id);
        Account account = accountRepository.getOne(id);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();
        root.put("server", account.getIp());
        root.put("server_port", account.getPort());
        root.put("password", account.getPassword());
        root.put("method", account.getMethod());
        root.put("local_address", "127.0.0.1");
        root.put("local_port", 1080);
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);

        ResponseEntity entity = ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + id + ".json")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(json);
        return R.builder().data(entity).build();
    }
}
