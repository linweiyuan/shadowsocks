package com.linweiyuan.shadowsocks.exception;

import com.linweiyuan.commons.model.ApiCode;
import com.linweiyuan.commons.model.R;
import com.linweiyuan.commons.util.ExceptionUtil;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler(Throwable.class)
    public R handleException(Throwable t) {
        ExceptionUtil.print(t, "com.linweiyuan");
        return R.builder().code(ApiCode.ERR.getValue()).msg("系统异常 -> " + t.getLocalizedMessage()).build();
    }
}
