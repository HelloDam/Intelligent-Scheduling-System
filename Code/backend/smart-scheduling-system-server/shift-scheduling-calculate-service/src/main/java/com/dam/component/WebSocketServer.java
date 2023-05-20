package com.dam.component;

import com.dam.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author websocket服务
 */
@ServerEndpoint(value = "/shift-scheduling-calculate-service/imserver/{token}")
@Component//将WebSocketServer注册为spring的一个bean
public class WebSocketServer {

    private static final Logger log = LoggerFactory.getLogger(WebSocketServer.class);

    /**
     * 记录当前在线连接的客户端的session
     */
    public static final Map<String, Session> tokenAndSessionMap = new ConcurrentHashMap<>();

    /**
     * 浏览器和服务端连接建立成功之后会调用这个方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) {
        tokenAndSessionMap.put(token, session);
        log.info("有新用户加入，username={}, 当前在线人数为：{}", JwtUtil.getUsername(token), tokenAndSessionMap.size());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session, @PathParam("token") String token) {
        String username = JwtUtil.getUsername(token);
        tokenAndSessionMap.remove(username);
        log.info("有一连接关闭，移除username={}的用户session, 当前在线人数为：{}", username, tokenAndSessionMap.size());
    }

    /**
     * 发生错误的时候会调用这个方法
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }

    /**
     * 服务端发送消息给客户端
     */
    public void sendMessage(String message, Session toSession) {
        try {
            log.info("服务端给客户端[{}]发送消息{}", toSession.getId(), message);
            toSession.getBasicRemote().sendText(message);
        } catch (Exception e) {
            log.error("服务端发送消息给客户端失败", e);
        }
    }

}
