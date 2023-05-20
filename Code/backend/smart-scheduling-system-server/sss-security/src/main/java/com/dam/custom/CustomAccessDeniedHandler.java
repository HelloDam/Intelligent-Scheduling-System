package com.dam.custom;

import com.dam.model.result.R;
import com.dam.utils.ResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 自定义没有权限的报错信息，默认是报403
 */
@Component//交给spring管理
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // 获取请求的URI
        String uri = request.getRequestURI();
        // 获取请求的方法
        String method = request.getMethod();
        // 获取当前用户的用户名
        String username = request.getRemoteUser();
        // 获取用户的IP地址
        String ip = request.getRemoteAddr();
        // 获取用户的浏览器类型
        String userAgent = request.getHeader("User-Agent");
        // 构造错误信息
        String errorMsg = "没有权限访问当前资源：" + uri + " (" + method + ")";
        ResponseUtil.out(response, R.error(403, errorMsg));
    }


}

