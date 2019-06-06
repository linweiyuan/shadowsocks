package com.linweiyuan.shadowsocks.common;

public class Constant {
    public static final int PING_TIMEOUT = 3000; // 3秒
    public static final String ACCOUNT_STATUS_ENABLE = "可用";
    public static final String ACCOUNT_STATUS_DISABLE = "被墙";
    public static final String API_SSR_TOOL = "https://www.ssrtool.com/tool/api/free_ssr?page=1&limit=80"; // 超过80会返回"Hello Spider!"，并且有Google reCAPTCHA
    public static final String HTTP_PROXY_SERVER = "172.17.0.1";
    public static final int HTTP_PROXY_PORT = 8118;
}
