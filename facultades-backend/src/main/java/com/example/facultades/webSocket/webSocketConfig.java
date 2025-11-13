package com.example.facultades.webSocket;

import com.example.facultades.dto.MensajeDTO;
import com.example.facultades.dto.MensajeGeneralRecord;
import com.example.facultades.enums.Socket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class webSocketConfig implements WebSocketMessageBrokerConfigurer {


    @Value("${SERVIDOR}")
    private String servidor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry){
        registry.enableSimpleBroker(Socket.TOPICO_PRINCIPAL.getRuta(), Socket.TOPICO_PERSONAL.getRuta());
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void  registerStompEndpoints(StompEndpointRegistry registry){
        //http://vps-4741837-x.dattaweb.com
        registry.addEndpoint("websocket").setAllowedOrigins("http://vps-4741837-x.dattaweb.com", "http://localhost:4200").withSockJS();;
    }


}
