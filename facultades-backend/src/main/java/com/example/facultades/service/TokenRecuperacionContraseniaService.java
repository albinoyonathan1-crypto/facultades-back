package com.example.facultades.service;

import com.example.facultades.dto.BaseDTO;
import com.example.facultades.dto.TokenRecuperacionContraseniaDTO;
import com.example.facultades.generics.GenericService;
import com.example.facultades.model.TokenRecuperacionContrasenia;
import com.example.facultades.model.TokenVerificacionEmail;
import com.example.facultades.model.Usuario;
import com.example.facultades.repository.ITokenRecuperacionContraseniaRepository;
import com.example.facultades.repository.ITokenVerificacionEmailRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TokenRecuperacionContraseniaService extends GenericService<TokenRecuperacionContrasenia, Long> implements ITokenRecuperacionContraseniaService{

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ITokenRecuperacionContraseniaRepository tokenRepository;

    @Autowired
    private IEmailService emailService;


    @Override
    public BaseDTO<TokenRecuperacionContrasenia> convertirDTO(TokenRecuperacionContrasenia entiendad) {
        return modelMapper.map(entiendad, TokenRecuperacionContraseniaDTO.class);
    }

    @Override
    public TokenRecuperacionContrasenia converirEntidad(BaseDTO<TokenRecuperacionContrasenia> DTO) {
        return modelMapper.map(DTO, TokenRecuperacionContrasenia.class);
    }

    @Override
    public TokenRecuperacionContrasenia findByToken(String token) {
        return tokenRepository.findByToken(token);
    }

    @Transactional
    @Override
    public void actualizarYEnviarToken(Long id) {
        TokenRecuperacionContrasenia tokenRecuperacionContrasenia = tokenRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "TokenVerificacionEmail no encontrado con id: " + id));

        String token = UUID.randomUUID().toString();
        tokenRecuperacionContrasenia.setFechaExpiracion(LocalDateTime.now().plusMinutes(30));
        tokenRecuperacionContrasenia.setToken(token);

        tokenRepository.save(tokenRecuperacionContrasenia);

        try {
            emailService.enviarCorreoRecuperacionContrasena(tokenRecuperacionContrasenia.getUsuario().getUsername(),
                    tokenRecuperacionContrasenia.getToken(), tokenRecuperacionContrasenia.getId());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error al enviar el correo de recuperación: " + e.getMessage(), e);
        }
    }

    @Override
    public TokenRecuperacionContrasenia generarTokenVerificacion(Usuario usuario) {
        String token = UUID.randomUUID().toString();
        TokenRecuperacionContrasenia verificationToken = new TokenRecuperacionContrasenia();
        verificationToken.setToken(token);
        verificationToken.setUsuario(usuario);
        verificationToken.setFechaExpiracion(LocalDateTime.now().plusMinutes(30));
         return tokenRepository.save(verificationToken);
    }



    @Override
    public TokenRecuperacionContrasenia actualizarToken(TokenRecuperacionContrasenia tokenRecuperacionContrasenia) {
        String token = UUID.randomUUID().toString();
        tokenRecuperacionContrasenia.setToken(token);
        tokenRecuperacionContrasenia.setFechaExpiracion(LocalDateTime.now().plusMinutes(30));
       return tokenRepository.save(tokenRecuperacionContrasenia);
    }

    @Override
    public TokenRecuperacionContrasenia findTokenRCbyUsuarioId(Long usuarioId){
        return tokenRepository.findTokenRCbyUsuarioId(usuarioId);
    }
}
