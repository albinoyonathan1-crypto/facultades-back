package com.example.facultades.controller;

import com.example.facultades.dto.ImagenUsuario;
import com.example.facultades.service.WebSocketSessionTracker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/socketConexiones")
public class WebSocketConexiones {

    @Autowired
    private WebSocketSessionTracker webSocketSessionTracker;

    @GetMapping("/obtener")
    public ResponseEntity<Set<String>> obtenerConexiones(){
        Map<String, String> sesionesActivas = webSocketSessionTracker.getActiveSessions();
        Set<String> listaIds = new HashSet<>(sesionesActivas.values());
        //System.out.println(listaIds);

        return  ResponseEntity.ok(listaIds);
    }

}
