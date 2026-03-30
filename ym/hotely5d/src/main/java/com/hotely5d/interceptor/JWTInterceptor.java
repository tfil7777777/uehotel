package com.hotely5d.interceptor;

import com.hotely5d.entity.model.Result;
import com.hotely5d.entity.model.StatusCode;
import com.hotely5d.utils.JWTUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JWTInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Result result = null;

        //获取请求头中的令牌
        String token = request.getHeader("Authorization");

        // 检查token是否为空或空白
        if (token == null || token.trim().isEmpty()) {
            result = new Result(StatusCode.TOKENERROR, false, "未登录或登录已过期，请重新登录！");
        } else {
            try {
                // 处理Bearer token格式
                if (token.startsWith("Bearer ")) {
                    token = token.substring(7);
                }

                // 再次检查token是否为空
                if (token.trim().isEmpty()) {
                    result = new Result(StatusCode.TOKENERROR, false, "token无效");
                } else {
                    //验证令牌
                    JWTUtils.verifyToken(token);
                    //验证通过，放行请求
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                // 根据不同的异常类型返回不同的错误信息
                String errorMsg = "未登录或登录已过期，请重新登录！";
                if (e instanceof com.auth0.jwt.exceptions.TokenExpiredException) {
                    errorMsg = "token已过期，请重新登录";
                } else if (e instanceof com.auth0.jwt.exceptions.SignatureVerificationException) {
                    errorMsg = "token签名验证失败";
                } else if (e instanceof com.auth0.jwt.exceptions.AlgorithmMismatchException) {
                    errorMsg = "token算法不一致";
                } else if (e instanceof com.auth0.jwt.exceptions.JWTDecodeException) {
                    errorMsg = "token格式错误";
                }
                result = new Result(StatusCode.TOKENERROR, false, errorMsg);
            }
        }

        //采用jackson方式将map转化为json 字符串
        String json = new ObjectMapper().writeValueAsString(result);
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(json);
        return false;
    }
}
