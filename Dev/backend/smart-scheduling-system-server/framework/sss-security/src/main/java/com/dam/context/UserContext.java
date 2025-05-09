package com.dam.context;

import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.Optional;

/**
 * @Author dam
 * @create 2024/4/2 16:12
 */
public class UserContext {
    /**
     * 定义一个私有的、静态的ThreadLocal变量，类型为UserInfoDTO，用于存储当前线程关联的用户信息
     */
    private static final ThreadLocal<UserInfoDTO> USER_THREAD_LOCAL = new TransmittableThreadLocal<>();

    /**
     * 设置用户至上下文
     *
     * @param user 用户详情信息
     */
    public static void setUser(UserInfoDTO user) {
        USER_THREAD_LOCAL.set(user);
    }

    /**
     * 获取上下文中用户 ID
     *
     * @return 用户 ID
     */
    public static String getUserId() {
        UserInfoDTO userInfoDTO = USER_THREAD_LOCAL.get();
        // 使用Optional进行空值安全处理：如果userInfoDTO不为空，则提取其userId属性并返回；否则返回null
        return Optional.ofNullable(userInfoDTO)
                .map(UserInfoDTO::getUserId)
                .orElse(null);
    }

    /**
     * 获取上下文中用户名称
     *
     * @return 用户名称
     */
    public static String getUsername() {
        UserInfoDTO userInfoDTO = USER_THREAD_LOCAL.get();
        return Optional.ofNullable(userInfoDTO)
                .map(UserInfoDTO::getUserName)
                .orElse(null);
    }

    /**
     * 清理用户上下文
     */
    public static void removeUser() {
        // 从ThreadLocal变量中移除当前线程关联的用户信息，释放资源
        USER_THREAD_LOCAL.remove();
    }
}
