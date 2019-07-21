package com.linweiyuan.shadowsocks.exception;

import com.linweiyuan.commons.model.ApiCode;
import com.linweiyuan.commons.model.X;
import com.linweiyuan.commons.util.ExceptionUtil;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler(Throwable.class)
    public X handleException(Throwable t) {
        ExceptionUtil.print(t, "com.linweiyuan");
        return X.builder().code(ApiCode.ERR.getValue()).msg("系统异常 -> " + t.getLocalizedMessage()).build();
    }
}
