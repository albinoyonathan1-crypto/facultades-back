package com.example.facultades.service;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WebSocketSessionTracker {

    private final Map<String, String> sesionesActivas = new ConcurrentHashMap<>();

    private final SimpMessagingTemplate messagingTemplate;

    // Inyección de SimpMessagingTemplate para enviar mensajes a los topics
    public WebSocketSessionTracker(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public  void handleWebSocketConnectListener(SessionConnectedEvent event) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String sessionId = headers.getSessionId();

        MessageHeaderAccessor headerAccessor =
                MessageHeaderAccessor.getAccessor(event.getMessage().getHeaders(),
                        MessageHeaderAccessor.class);

        // Obtener el StompHeaderAccessor desde el mensaje de conexión
        StompHeaderAccessor stompHeaderAccessor = MessageHeaderAccessor.getAccessor(
                (Message<?>) headerAccessor.getHeader("simpConnectMessage"),
                StompHeaderAccessor.class);

        // Obtener el userId desde los encabezados nativos
        String userId = stompHeaderAccessor.getNativeHeader("userId").get(0);

        if (userId != null) {
            // Verificar si ya existe la sesión del usuario
            if (!sesionesActivas.containsValue(userId)) {
                sesionesActivas.put(sessionId, userId);

                messagingTemplate.convertAndSend("/tema/estadoConexiones",  sesionesActivas.values());
            }
        }

    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        String userId = sesionesActivas.remove(sessionId);

        if (userId != null) {

            messagingTemplate.convertAndSend("/tema/estadoConexiones", sesionesActivas.values());
        }
    }

    public Map<String, String> getActiveSessions() {
       // messagingTemplate.convertAndSend("/tema/estadoConexiones", "conectado " + sesionesActivas.values());
        return sesionesActivas;
    }



}
