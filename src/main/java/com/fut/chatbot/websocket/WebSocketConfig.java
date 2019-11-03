package com.fut.chatbot.websocket;

import com.fut.chatbot.repo.UserRepo;
import java.lang.invoke.MethodHandles;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.handler.invocation.HandlerMethodReturnValueHandler;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chatbot-web-socket", "/send").setHandshakeHandler(
                new AssignPrincipalHandshakeHandler()).withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/tag", "/reply");
        config.setUserDestinationPrefix("/private");
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setSendTimeLimit(15 * 1000).setSendBufferSizeLimit(512 * 1024);
    }

    // various listeners for debugging purpose
    @EventListener
    public void handleSubscribeEvent(SessionSubscribeEvent event) {
        LOG.info("<==> handleSubscribeEvent: username=" + event.getUser().getName() + ", event=" + event);
    }

    @EventListener
    public void handleConnectEvent(SessionConnectEvent event) {
        LOG.info("===> handleConnectEvent: username=" + event.getUser().getName() + ", event=" + event);
    }

    @EventListener
    public void handleDisconnectEvent(SessionDisconnectEvent event) {
        LOG.info("<=== handleDisconnectEvent: username=" + event.getUser().getName() + ", event=" + event);
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        System.out.println("configureMessageConverters");
        return WebSocketMessageBrokerConfigurer.super.configureMessageConverters(messageConverters); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
        System.out.println("addReturnValueHandlers");
        WebSocketMessageBrokerConfigurer.super.addReturnValueHandlers(returnValueHandlers); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        System.out.println("addArgumentResolvers");
        WebSocketMessageBrokerConfigurer.super.addArgumentResolvers(argumentResolvers); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        System.out.println("configureClientOutboundChannel");
        WebSocketMessageBrokerConfigurer.super.configureClientOutboundChannel(registration); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        System.out.println("configureClientInboundChannel");
        WebSocketMessageBrokerConfigurer.super.configureClientInboundChannel(registration); //To change body of generated methods, choose Tools | Templates.
    }
    
    

}
