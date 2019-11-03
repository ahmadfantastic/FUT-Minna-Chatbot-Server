package com.fut.chatbot.websocket;

import com.fut.chatbot.util.Constants;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

/**
 * Assign a username as principal for each websocket client. This is needed to
 * be able to communicate with a specific client.
 */
public class AssignPrincipalHandshakeHandler extends DefaultHandshakeHandler {

    private static final String ATTR_PRINCIPAL = "__principal__";

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        final String phone;
        if (!attributes.containsKey(ATTR_PRINCIPAL)) {
            String key = request.getHeaders().getFirst("key");
            String code = request.getHeaders().getFirst("code");
            phone = request.getHeaders().getFirst("phone");
            if (key != null && key.equals(Constants.KEY)){
                System.out.println("[Phone : Code] = {" + phone + " : " + code + "}");
                attributes.put(ATTR_PRINCIPAL, phone);
            }
        } else {
            phone = (String) attributes.get(ATTR_PRINCIPAL);
        }
        return () -> phone;
    }
}
