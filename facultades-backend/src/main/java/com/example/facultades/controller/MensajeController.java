package com.example.facultades.controller;

import com.example.facultades.dto.ComentarioDTO;
import com.example.facultades.dto.MensajeDTO;
import com.example.facultades.dto.MensajeGeneralRecord;
import com.example.facultades.dto.MensajeRetornoSimple;
import com.example.facultades.excepciones.FallBackEncriptarDesencriptarException;
import com.example.facultades.generics.ControllerGeneric;
import com.example.facultades.generics.GenericService;
import com.example.facultades.model.Comentario;
import com.example.facultades.model.Mensaje;
import com.example.facultades.service.IEncriptacionMensajeServiceClient;
import com.example.facultades.service.MensajeService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/mensaje")
public class MensajeController extends ControllerGeneric<Mensaje, MensajeDTO,Long> {

    @Autowired
    GenericService<Mensaje, Long> mensajeGenericService;

    @Autowired
    MensajeService mensajeService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private IEncriptacionMensajeServiceClient encriptacionMensajeServiceClient;

    @PostMapping("/guardar")
    @CircuitBreaker(name = "encriptacionMensajeServiceClient", fallbackMethod = "fallbackEncriptarMensajes")
    @Retry(name = "encriptacionMensajeServiceClient")
    public ResponseEntity<MensajeDTO> guardar(@RequestBody MensajeDTO mensajeDTO) {
        Mensaje mensaje = mensajeService.converirEntidad(mensajeDTO);

        // Encriptar contenido
        String contenidoEncriptado = encriptacionMensajeServiceClient.encriptar(mensaje.getContenido()).getBody();
        if (contenidoEncriptado == null) {
            return ResponseEntity.badRequest().body(null); // Manejo de error si la encriptación falla
        }
        mensaje.setContenido(contenidoEncriptado);
        Mensaje mensajeGuardado = mensajeGenericService.save(mensaje);

        //MensajeDTO mensajeDTO1 = (MensajeDTO) mensajeGenericService.convertirDTO(mensajeGuardado);
        mensajeDTO.setId(mensajeGuardado.getId());

        mensajeService.agregarIdReceptorAListaIdsEmisor(mensajeDTO.getIdEmisor(), mensajeDTO.getIdReceptor());
        messagingTemplate.convertAndSend("/tema/actualizarListaMensajeRecibido/" + mensajeDTO.getIdReceptor(), mensajeDTO);
        return ResponseEntity.ok(mensajeDTO);
    }

    public ResponseEntity<MensajeDTO> fallbackEncriptarMensajes(MensajeDTO mensajeDTO, Throwable t) {
        throw new FallBackEncriptarDesencriptarException();
    }

    public ResponseEntity<List<MensajeDTO>> fallbackDesencriptarMensajes(Long idEmisor, Long idReceptor, Throwable t) {
        throw new FallBackEncriptarDesencriptarException();
    }



    @GetMapping("/getMensaesEmisorReceptor/{idEmisor}/{idReceptor}")
    @CircuitBreaker(name = "encriptacionMensajeServiceClient", fallbackMethod = "fallbackDesencriptarMensajes")
    @Retry(name = "encriptacionMensajeServiceClient")
    public ResponseEntity<List<MensajeDTO>> getEmisorReceptor(@PathVariable Long idEmisor, @PathVariable Long idReceptor) {
        List<MensajeDTO> listaMensajeSEncriptadosDTO = mensajeService.findByEmisorAndReceptor(idEmisor, idReceptor);
        List<MensajeDTO> listaMensajesDesencriptadosDTO = encriptacionMensajeServiceClient.desencriptarLista(listaMensajeSEncriptadosDTO).getBody();
        return ResponseEntity.ok(listaMensajesDesencriptadosDTO);
    }

    @PostMapping("/actualizarMensajeLeido/{idEmisor}")
    public void actualizarMensajeLeido(@PathVariable Long idEmisor) {
        messagingTemplate.convertAndSend("/tema/actualizarMensajeLeido/" + idEmisor, true);
    }

    @MessageMapping("/chatGeneral")
    @SendTo("/tema/chatGeneral")
    public MensajeGeneralRecord enviarMensajeChatGeneral(@Payload MensajeGeneralRecord mensaje) {
        return mensaje;
    }

    @PutMapping("/marcarMensajeLeido/{idMensaje}")
    public ResponseEntity<MensajeRetornoSimple> marcarMensajeLeido(@PathVariable Long idMensaje) {
        Optional<Mensaje> mensajeOptional = mensajeService.findById(idMensaje);

        if (mensajeOptional.isPresent()) {
            Mensaje mensaje = mensajeOptional.get();
            mensaje.setLeida(true);
            mensajeService.save(mensaje);
            return ResponseEntity.ok(new MensajeRetornoSimple("Mensaje marcado como leído"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MensajeRetornoSimple("Error al intentar marcar mensaje como leido"));
        }
    }


}
