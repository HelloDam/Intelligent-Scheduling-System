package com.dam.utils;

import io.jsonwebtoken.*;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * 生成JSON Web Token的工具类
 */
public class JwtUtil {

    /**
     * JWT的默认过期时间，单位为毫秒。这里设定为一年（365天）
     */
    private static long tokenExpiration = 365 * 24 * 60 * 60 * 1000;
    /**
     * 在实际应用中，应使用随机生成的字符串
     */
    private static String tokenSignKey = "dsahdashoiduasguiewu23114";

    /**
     * 从给定的JWT令牌中提取指定参数名对应的值。
     *
     * @param token     需要解析的JWT令牌字符串
     * @param paramName 要提取的参数名
     * @return 参数值（字符串形式），如果令牌为空、解析失败或参数不存在，则返回null
     */
    public static String getParam(String token, String paramName) {
        try {
            if (StringUtils.isEmpty(token)) {
                return null;
            }
            // 使用提供的密钥解析并验证JWT
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
            // 获取JWT的有效载荷（claims），其中包含了所有声明（参数）
            Claims claims = claimsJws.getBody();
            // 提取指定参数名对应的值
            Object param = claims.get(paramName);
            // 如果参数值为空，则返回null；否则将其转换为字符串并返回
            return param == null ? null : param.toString();
        } catch (Exception e) {
            // 记录解析过程中的任何异常，并返回null
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据用户信息生成一个新的JWT令牌。
     *
     * @param userId
     * @param username
     * @return
     */
    public static String createToken(Long userId, String username, Long enterpriseId, Long storeId, int userType) {
//        System.out.println("createToken userType:" + userType);
        // 使用Jwts.builder()构建JWT
        String token = Jwts.builder()
                // 设置JWT的主题（subject），此处为常量"AUTH-USER"
                .setSubject("AUTH-USER")
                // 设置过期时间，当前时间加上预设的过期时间（tokenExpiration）
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))
                // 有效载荷
                .claim("userId", userId)
                .claim("username", username)
                .claim("enterpriseId", enterpriseId)
                .claim("storeId", storeId)
                .claim("userType", userType)
                // 使用HS512算法和指定密钥对JWT进行加密
                .signWith(SignatureAlgorithm.HS512, tokenSignKey)
                // 使用GZIP压缩算法压缩JWT字符串，将字符串变成一行来显示
                .compressWith(CompressionCodecs.GZIP)
                // 完成构建并生成紧凑格式的JWT字符串
                .compact();
        return token;
    }


    public static String getUserId(String token) {
        return getParam(token, "userId");
    }

    public static String getUsername(String token) {
        return getParam(token, "username");
    }

    public static String getEnterpriseId(String token) {
        return getParam(token, "enterpriseId");
    }

    public static String getStoreId(String token) {
        return getParam(token, "storeId");
    }

    public static String getUserType(String token) {
        return getParam(token, "userType");
    }

}