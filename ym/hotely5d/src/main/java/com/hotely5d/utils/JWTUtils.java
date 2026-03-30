package com.hotely5d.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Map;

public class JWTUtils {
    //参考：https://www.cnblogs.com/spring8889/p/15775134.html
    //签名密钥
    private static final String SIGNKEY = "!@#$%QWER123456121234521";//这个随便写都可以

    /**
     * 生成token
     * header.payload.signature
     */
    public static String getToken(Map<String, String> map) {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.DATE, 15);//token 默认15天有效

        //创建JWTBuilder
        JWTCreator.Builder builder = JWT.create();

        //Payload配置
        map.forEach((k, v) -> {
            builder.withClaim(k, v);
        });

        String token = builder.withExpiresAt(instance.getTime())   //过期时间设置
                .sign(Algorithm.HMAC256(SIGNKEY));//签名
        return token;
    }

    /**
     * 验证 token 合法性
     */
    public static DecodedJWT verifyToken(String token) {
        // 验证token是否为空
        if (!StringUtils.hasText(token)) {
            throw new JWTDecodeException("Token为空");
        }

        // 检查token格式（Bearer token格式）
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        //验证合法性
        DecodedJWT verify = JWT.require(Algorithm.HMAC256(SIGNKEY)).build().verify(token);
        return verify;
    }

    /**
     * 从token中获取用户id
     *
     * @param request
     * @return 用户ID，如果token无效返回null
     */
    public static Long getUserId(HttpServletRequest request) {
        try {
            String token = getTokenFromRequest(request);
            if (token == null) {
                return null;
            }
            DecodedJWT verify = verifyToken(token);
            return verify.getClaim("id").as(Long.class);//从token中获取用户id
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从token中获取用户角色
     *
     * @param request
     * @return 用户角色，如果token无效返回null
     */
    public static Long getRole(HttpServletRequest request) {
        try {
            String token = getTokenFromRequest(request);
            if (token == null) {
                return null;
            }
            DecodedJWT verify = verifyToken(token);
            return verify.getClaim("role").as(Long.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从请求中获取token（支持Bearer格式）
     */
    public static String getTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (!StringUtils.hasText(token)) {
            return null;
        }

        // 支持Bearer token格式
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        return token;
    }

    /**
     * 验证token是否有效
     */
    public static boolean isTokenValid(String token) {
        try {
            if (!StringUtils.hasText(token)) {
                return false;
            }

            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            verifyToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从token中获取指定claim的值
     */
    public static <T> T getClaimFromToken(String token, String claimName, Class<T> clazz) {
        try {
            if (!StringUtils.hasText(token)) {
                return null;
            }

            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            DecodedJWT decodedJWT = verifyToken(token);
            return decodedJWT.getClaim(claimName).as(clazz);
        } catch (Exception e) {
            return null;
        }
    }
}
