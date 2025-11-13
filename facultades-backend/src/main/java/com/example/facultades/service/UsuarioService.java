package com.example.facultades.service;

import com.example.facultades.dto.BaseDTO;
import com.example.facultades.dto.MensajeRetornoSimple;
import com.example.facultades.dto.UsuarioDTO;
import com.example.facultades.dto.UsuarioRecord;
import com.example.facultades.enums.DuracionToken;
import com.example.facultades.enums.MensajeNotificacionAdmin;
import com.example.facultades.enums.NombreRepositorio;
import com.example.facultades.enums.Socket;
import com.example.facultades.excepciones.ContraseniaIncorrectaException;
import com.example.facultades.excepciones.UsuarioNoEncontradoException;
import com.example.facultades.generics.GenericService;
import com.example.facultades.generics.IgenericService;
import com.example.facultades.model.*;
import com.example.facultades.repository.IUsuarioRepository;

import com.example.facultades.util.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.RandomStringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class UsuarioService extends GenericService<Usuario, Long> implements IUsuarioService, IEntidadAsociable<Usuario> {

    @Autowired
    private IAsociarEntidades asociarEntidades;

    @Autowired
    private IRepositoryFactory repositoryFactory;

    @Autowired
    private IUsuarioRepository usuarioRepo;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private INotificacionService notificacionService;

    @Autowired
    private IgenericService<RefreshToken, Long> refreshTokenService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    @Lazy
    private IRolService rolService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private IgenericService<TokenVerificacionEmail, Long> verificacionEmailService;

    @Autowired
    private IEmailService emailService;

    @Autowired
    private IgenericService<TokenRecuperacionContrasenia, Long> tokenRecuperacionContraseniaService;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Override
    public String encriptPassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }

    @Override
    public Usuario buscarUsuarioPorNombre(String username){
        return usuarioRepo.buscarUsuarioPorNombre(username);
    }

    @Override
    @Transactional
    public Usuario saveUserOauth(String email, String nick, String imagenPerfil) {
        Usuario usuario = new Usuario();
        usuario.setUsername(email);
        usuario.setNick(nick);
        usuario.setImagen(imagenPerfil);
        usuario.setEmailVerified(true);
        usuario.setRefreshToken(crearRefreshToken(usuario));
        List<Rol> roleList = new ArrayList<>();
        Rol rolPorDefecto = rolService.findRolByName("USER");
        Rol managedRole = entityManager.merge(rolPorDefecto);

        roleList.add(managedRole);

        usuario.setEnable(true);
        usuario.setAccountNotExpired(true);
        usuario.setAccountNotLocked(true);
        usuario.setCredentialNotExpired(true);
        usuario.setUsername(email);
        usuario.setPassword(this.encriptPassword(this.generatedPassword()));
        usuario.setListaRoles(roleList);
        return  usuarioRepo.save(usuario);
        //return this.save(usuario);
    }

    public String crearNick(String email) {
        StringBuilder nick = new StringBuilder();
        int tamanioEmail = email.length();

        for (int i = 0; i < tamanioEmail; i++) {
            char caracter = email.charAt(i);
            if (caracter == '@') {
                break;
            }
            nick.append(caracter);
        }
        return nick.toString();
    }


    @Override
    public Optional<Usuario> findUserEntityByusername(String username) {
        //IUsuarioRepository IUsuarioRepository = (IUsuarioRepository) repositoryFactory.generarRepositorio(NombreRepositorio.USUARIO.getRepoName());
        return usuarioRepo.findUserEntityByusername(username);
    }

    @Override
    public List<Usuario> getUsuarioListByRole(String nombreRol) {
        return usuarioRepo.findUserEntityByListaRolesNombreRol(nombreRol);
    }

    public String generatedPassword(){
        return RandomStringUtils.randomAlphanumeric(8);
    }

    @PreAuthorize("hasAnyRole('ADMIN',)")
    @Override
    public List<Usuario> getAll() {
        return super.getAll();
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Override
    public Optional<Usuario> findById(Long aLong) {
        return super.findById(aLong);
    }

    @Override
    public Usuario save(Usuario usuario) {
        configurarUsuario(usuario);
        asociarRoles(usuario);
        encriptarContrasenia(usuario);
        Usuario usuarioGuardado = guardarUsuario(usuario);
        if (usuarioGuardado.getId() != null) {
            //manejarNotificacionCreacion(usuarioGuardado);
            // Generar y guardar el token de verificación
            TokenVerificacionEmail verificationToken = generarTokenVerificacion(usuarioGuardado);
            verificacionEmailService.save(verificationToken);

            // Enviar el correo de verificación
            enviarCorreoVerificacion(usuarioGuardado, verificationToken);
        }
        return usuarioGuardado;
    }

    public TokenVerificacionEmail generarTokenVerificacion(Usuario usuario) {
        String token = UUID.randomUUID().toString();
        TokenVerificacionEmail verificationToken = new TokenVerificacionEmail();
        verificationToken.setToken(token);
        verificationToken.setUsuario(usuario);
        verificationToken.setFechaExpiracion(LocalDateTime.now().plusHours(24));
        return verificationToken;
    }

    private void enviarCorreoVerificacion(Usuario usuario, TokenVerificacionEmail verificationToken) {
        // Enviar el correo de verificación con el token
        emailService.enviarCorreoVerificacionEmail(usuario.getUsername(), verificationToken.getToken(), verificationToken.getId());
    }

    private void configurarUsuario(Usuario usuario) {
        usuario.setEmailVerified(false);
        usuario.setEnable(true);
        usuario.setAccountNotExpired(true);
        usuario.setAccountNotLocked(true);
        usuario.setCredentialNotExpired(true);
        usuario.setRefreshToken(crearRefreshToken(usuario));
    }

    private void asociarRoles(Usuario usuario) {
        usuario.setListaRoles(Arrays.asList(rolService.findRolByName("USER")));
        this.asociar(usuario);
    }

    @Override
    public void encriptarContrasenia(Usuario usuario) {
        usuario.setPassword(this.encriptPassword(usuario.getPassword()));
    }

    @Override
    public void infraccionarUsuario(Long idUsuario) {
        Optional<Usuario> usuarioBuscado = this.findById(idUsuario);
        if (usuarioBuscado.isPresent()) {
            Usuario usuario = usuarioBuscado.get();
            if(usuario.getInfracciones() < 3){
                // Incrementar las infracciones
                usuario.setInfracciones(usuario.getInfracciones() + 1);
                String mensajeHtml = "<div style='font-family: Arial, sans-serif; color: #333; padding: 20px; border: 1px solid #ddd; border-radius: 5px; text-align: center;'>"
                        + "<h2 style='color: #cc0000;'>Infracción en FacusArgs</h2>"
                        + "<h3 style='color: #cc0000;'>Notificación de Infracción</h3>"
                        + "<p>Tu cuenta ha recibido una infracción por romper las normas de nuestra app.</p>"
                        + "<p>Actualmente tienes un total de <strong>" + usuario.getInfracciones() + " infracciones</strong>.</p>"
                        + "<p>Recuerda que puedes acumular un máximo de 3 infracciones.</p>"
                        + "<p>Te recomendamos que revises las normas de la aplicación para evitar futuras sanciones.</p>"
                        + "<p style='color: #777;'>Este es un mensaje automático. No es necesario responder.</p>"
                        + "</div>";

                emailService.enviarEmail(usuario.getUsername(),"Infracción", mensajeHtml);
            }
            // Si el usuario ha superado las 3 infracciones, bloquear la cuenta
            if (usuario.getInfracciones() >= 3) {
                String mensajeHtml = "<div style='font-family: Arial, sans-serif; color: #333; padding: 20px; border: 1px solid #ddd; border-radius: 5px; text-align: center;'>"
                        + "<h2 style='color: #cc0000;'>Tu Cuenta de FacusArg Ha Sido Baneada</h2>"
                        + "<h3 style='color: #cc0000;'>Notificación de Baneo</h3>"
                        + "<p>Tu cuenta ha sido baneada debido a múltiples infracciones en nuestra aplicación.</p>"
                        + "<p>Lamentablemente, no podrás acceder a tu cuenta hasta que se resuelva esta situación.</p>"
                        + "<p>Te recomendamos que te pongas en contacto con nuestro soporte para obtener más información sobre el baneo.</p>"
                        + "<p style='color: #777;'>Este es un mensaje automático. No es necesario responder.</p>"
                        + "</div>";

                emailService.enviarEmail(usuario.getUsername(),"Baneo", mensajeHtml);
                usuario.setBaneada(true);
                usuario.setInfracciones(usuario.getInfracciones() + 1);
            }
            usuarioRepo.save(usuario);
        } else {
            throw new UsuarioNoEncontradoException("El usuario con ID " + idUsuario + " no fue encontrado.");
        }
    }


    @Override
    public void cambiarContrasenia(Long idUsuario, String nuevaContrasenia) throws Exception {
        // Buscar al usuario por ID
        Usuario usuario = usuarioRepo.findById(idUsuario)
                .orElseThrow(() -> new UsuarioNoEncontradoException());

        // Actualizar la contraseña con la nueva, encriptada
        usuario.setPassword(this.encriptPassword(nuevaContrasenia));
        usuarioRepo.save(usuario);
    }

    @Override
    public void actualizarContrasenia(Long idUsuario, String nuevaContrasenia, String contraseniaActual) {
        // Buscar al usuario por su ID
        Usuario usuario = usuarioRepo.findById(idUsuario)
                .orElseThrow(() -> new UsuarioNoEncontradoException("El usuario con ID " + idUsuario + " no fue encontrado"));

        // Verificar si la contraseña actual proporcionada es válida
        System.out.println(idUsuario + " " + nuevaContrasenia + " "+ contraseniaActual);
        if (!passwordEncoder.matches(contraseniaActual, usuario.getPassword())) {
            throw new ContraseniaIncorrectaException();
        }

        // Codificar la nueva contraseña y actualizarla en el usuario
        usuario.setPassword(passwordEncoder.encode(nuevaContrasenia));
        usuarioRepo.save(usuario); // Guardar los cambios en la base de datos
    }

    @Override
    public String buscarImagenPorIdUser(Long id) {
        return usuarioRepo.buscarImagenPorIdUser(id);
    }

    @Override
    public MensajeRetornoSimple findUsernamesByUniversidadId(Long universidadId) {
        String userName =  usuarioRepo.findFirstUsernameByUniversidadIdNative(universidadId);
        MensajeRetornoSimple retornoSimple = new MensajeRetornoSimple(userName);
        return  retornoSimple;
    }

    @Override
    public Usuario buscarUsuarioPorNick(String nick) {
        return usuarioRepo.buscarUsuarioPorNick(nick);
    }

    @Override
    public String obtenerRefreshTokenPorUsuario(Long idUsuario) {
        return usuarioRepo.obtenerRefreshTokenPorUsuario(idUsuario);
    }

    @Override
    public List<UsuarioRecord> buscarUsuariosPorListIds(List<Long> ids) {
        List<Usuario> usuarios =  usuarioRepo.buscarUsuariosPorListIds(ids);
        return usuarios.stream()
                .map(u -> new UsuarioRecord(u.getId(), u.getNick(), u.getImagen(), u.getIdsUsuariosNotificar()))
                .toList();
    }

    @Override
    public List<UsuarioRecord> buscarTodosLosUsuarios() {
        List<Usuario> usuarios = usuarioRepo.buscarTodosLosUsuarios();
        return usuarios.stream()
                .map(u -> new UsuarioRecord(u.getId(), u.getNick(), u.getImagen(), u.getIdsUsuariosNotificar()))
                .toList();
    }


    private Usuario guardarUsuario(Usuario usuario) {
        return usuarioRepo.save(usuario);
    }

    private void manejarNotificacionCreacion(Usuario usuarioGuardado) {
        Notificacion notificacion = new Notificacion();
        notificacion.setUsuario(true);
        Utili.manejarNotificacionAdmin(MensajeNotificacionAdmin.CREACION_USUARIO.getNotificacion(), usuarioGuardado, notificacionService, notificacion);
    }
//xD
    @Override
    public RefreshToken crearRefreshToken(Usuario usuario){
       // String token = jwtUtil.createRefreshToken(usuario.getUsername(), DuracionToken.REFRESH_TOKEN.getDuracion());
        String token = jwtUtil.createRefreshToken(usuario.getUsername(), DuracionToken.REFRESH_TOKEN.getDuracion());
        RefreshToken refreshToken = refreshTokenService.save(new RefreshToken(token));
        return refreshTokenService.save(refreshToken);
    }

    @Override
    public Usuario update(Usuario usuario) {
        this.asociar(usuario);
        return this.usuarioRepo.save(usuario);
    }

    @Override
    public BaseDTO<Usuario> convertirDTO(Usuario usuario) {
        UsuarioDTO usuarioDTO = modelMapper.map(usuario, UsuarioDTO.class);
        return usuarioDTO;
    }

    @Override
    public Usuario converirEntidad(BaseDTO<Usuario> DTO) {
        Usuario usuario = modelMapper.map(DTO, Usuario.class);
        return usuario;
    }

    @Override
    public void asociar(Usuario usuario) {
        usuario.setListaRoles(asociarEntidades.relacionar(usuario.getListaRoles(),repositoryFactory.generarRepositorio(NombreRepositorio.ROL.getRepoName())));
        usuario.setListaCalificacion(asociarEntidades.relacionar(usuario.getListaCalificacion(),repositoryFactory.generarRepositorio(NombreRepositorio.CALIFICACION.getRepoName())));
       // usuario.setListaRespuesta(asociarEntidades.relacionar(usuario.getListaRespuesta(), repositoryFactory.generarRepositorio(NombreRepositorio.RESPUESTA.getRepoName())));
        usuario.setListaReaccion(asociarEntidades.relacionar(usuario.getListaReaccion(),repositoryFactory.generarRepositorio(NombreRepositorio.REACCION.getRepoName())));
        usuario.setListaComentarios(asociarEntidades.relacionar(usuario.getListaComentarios(),repositoryFactory.generarRepositorio(NombreRepositorio.COMENTARIO.getRepoName())));
        usuario.setListaUniversidad(asociarEntidades.relacionar(usuario.getListaUniversidad(), repositoryFactory.generarRepositorio(NombreRepositorio.UNIVERSIDAD.getRepoName())));
    }

    public void eliminarNotificacionUsuario(Long usuarioId, Long usuarioNotificarId){
        usuarioRepo.eliminarNotificacionParaUsuarioId(usuarioId,usuarioNotificarId);
    }

}
