package com.example.facultades.service;

import com.example.facultades.dto.BaseDTO;
import com.example.facultades.dto.CarreraDTO;
import com.example.facultades.dto.ComentarioDTO;
import com.example.facultades.dto.TokenVerificacionEmailDTO;
import com.example.facultades.generics.GenericService;
import com.example.facultades.generics.IGenericRepository;
import com.example.facultades.generics.IgenericService;
import com.example.facultades.model.Carrera;
import com.example.facultades.model.Comentario;
import com.example.facultades.model.Rol;
import com.example.facultades.model.TokenVerificacionEmail;
import com.example.facultades.repository.ITokenVerificacionEmailRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TokenVerificacionEmailService extends GenericService<TokenVerificacionEmail, Long> implements ITokenVerificacionEmailService{

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ITokenVerificacionEmailRepository tokenRepository;

    //@Autowired
    //private IgenericService<TokenVerificacionEmail, Long> generciServiceTokenVerificacionEmail;

    @Autowired
    private IEmailService  emailService;

    @Override
    public BaseDTO<TokenVerificacionEmail> convertirDTO(TokenVerificacionEmail tokenVerificacionEmail) {
        return modelMapper.map(tokenVerificacionEmail, TokenVerificacionEmailDTO.class);
    }

    @Override
    public TokenVerificacionEmail converirEntidad(BaseDTO<TokenVerificacionEmail> DTO) {
        return modelMapper.map(DTO, TokenVerificacionEmail.class);
    }


    @Override
    public TokenVerificacionEmail findByToken(String token) {
        return tokenRepository.findByToken(token);
    }

    @Transactional
    @Override
    public void actualizarYEnviarToken(Long id) {
        TokenVerificacionEmail tokenVerificacionEmail = tokenRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "TokenVerificacionEmail no encontrado con id: " + id));

        String token = UUID.randomUUID().toString();
        tokenVerificacionEmail.setFechaExpiracion(LocalDateTime.now().plusMinutes(30));
        tokenVerificacionEmail.setToken(token);


        tokenRepository.save(tokenVerificacionEmail);

        try {
            emailService.enviarCorreoVerificacionEmail(tokenVerificacionEmail.getUsuario().getUsername(),
                    tokenVerificacionEmail.getToken(), tokenVerificacionEmail.getId());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error al enviar el correo de verificación: " + e.getMessage(), e);
        }
    }
}
