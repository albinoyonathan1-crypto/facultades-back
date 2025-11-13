package com.example.facultades.service;

import com.example.facultades.dto.MensajeDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "encriptacionMensajes", url = "http://localhost:8081")
public interface IEncriptacionMensajeServiceClient {

    @GetMapping("/app/encriptar")
    ResponseEntity<String> encriptar(@RequestParam("mensaje")String mensaje);

    @GetMapping("/app/desencriptar")
    ResponseEntity<String> desencriptar(@RequestParam("mensajeEncriptado") String mensajeEncriptado);

    @PostMapping("/app/desencriptarLista")
    ResponseEntity<List<MensajeDTO>> desencriptarLista(@RequestBody List<MensajeDTO> listaMensajesEncriptados);
}
