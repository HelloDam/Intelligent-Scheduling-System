package com.dam.utils;

import io.jsonwebtoken.*;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * 生成JSON Web令牌的工具类
 */
public class JwtUtil {

    /**
     * token的过期时间
     */
    private static long tokenExpiration = 365 * 24 * 60 * 60 * 1000;
    /**
     * 加密的秘钥：实际中使用随机字符串
     */
    private static String tokenSignKey = "123456";

    /**
     * 生成token字符串
     *
     * @param userId
     * @param username
     * @return
     */
/*    public static String createToken(Long userId, String username) {
        String token = Jwts.builder()
                .setSubject("AUTH-USER")
                //设置过期时间
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))
                //有效载荷
                .claim("userId", userId)
                .claim("username", username)
                //根据秘钥对编码进行加密处理
                .signWith(SignatureAlgorithm.HS512, tokenSignKey)
                //压缩字符串，将字符串变成一行来显示
                .compressWith(CompressionCodecs.GZIP)
                .compact();
        return token;
    }*/

    /**
     * 生成token字符串
     *
     * @param userId
     * @param username
     * @return
     */
/*    public static String createToken(String userId, String username) {
        String token = Jwts.builder()
                .setSubject("AUTH-USER")
                //设置过期时间
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))
                //有效载荷
                .claim("userId", userId)
                .claim("username", username)
                //根据秘钥对编码进行加密处理
                .signWith(SignatureAlgorithm.HS512, tokenSignKey)
                //压缩字符串，将字符串变成一行来显示
                .compressWith(CompressionCodecs.GZIP)
                .compact();
        return token;
    }*/

    public static String getUserId(String token) {
        return getParam(token, "userId");
    }

    public static String getUsername(String token) {
        return getParam(token, "username");
    }

    /**
     * 从token中获取相应参数
     *
     * @param token
     * @param paramName
     * @return
     */
    public static String getParam(String token, String paramName) {
        try {
            if (StringUtils.isEmpty(token)) {
                return null;
            }
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
            Claims claims = claimsJws.getBody();
            Object param = claims.get(paramName);
            if (param == null) {
                return null;
            } else {
                return claims.get(paramName) + "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 生成token字符串
     *
     * @param userId
     * @param username
     * @return
     */
    public static String createToken(Long userId, String username, Long enterpriseId, Long storeId, int userType) {
        System.out.println("createToken userType:"+userType);
        String token = Jwts.builder()
                .setSubject("AUTH-USER")
                //设置过期时间
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))
                //有效载荷
                .claim("userId", userId)
                .claim("username", username)
                .claim("enterpriseId", enterpriseId)
                .claim("storeId", storeId)
                .claim("userType", userType)
                //根据秘钥对编码进行加密处理
                .signWith(SignatureAlgorithm.HS512, tokenSignKey)
                //压缩字符串，将字符串变成一行来显示
                .compressWith(CompressionCodecs.GZIP)
                .compact();
        return token;
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