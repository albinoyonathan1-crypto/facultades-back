package com.example.facultades.service;

import com.example.facultades.dto.AuthLoguinRequestDTO;
import com.example.facultades.dto.AuthLoguinResponseDTO;
import com.example.facultades.enums.DuracionToken;
import com.example.facultades.excepciones.EmailNoEncontradoException;
import com.example.facultades.excepciones.EmailNoVerificadoException;
import com.example.facultades.generics.IgenericService;
import com.example.facultades.model.RefreshToken;
import com.example.facultades.util.JwtUtil;
import com.example.facultades.model.Usuario;
import com.example.facultades.repository.IUsuarioRepository;
import com.example.facultades.util.Utili;
import jdk.jshell.execution.Util;
import org.apache.catalina.core.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImp implements UserDetailsService {

    @Autowired
    private IUsuarioRepository usuarioRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private IgenericService<RefreshToken, Long> refreshTokenService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepo.findUserEntityByusername(username).orElseThrow(
                () -> new UsernameNotFoundException("El usuario " +username+ " no fue encontrado"));

        if(!usuario.isEmailVerified())
            throw new EmailNoVerificadoException();

        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();


        usuario.getListaRoles()
                .stream()
                .forEach(roles -> authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(roles.getNombreRol()))));

        usuario.getListaRoles()
                .stream()
                .flatMap(role -> role.getListaPermiso().stream())
                .forEach(permiso -> authorityList.add(new SimpleGrantedAuthority(permiso.getNombrePermiso())));
        //System.out.println(usuario);

        return new User(
                usuario.getUsername(),
                usuario.getPassword(),
                usuario.isEnable(),
                usuario.isAccountNotExpired(),
                usuario.isAccountNotLocked(),
                usuario.isCredentialNotExpired(),
                authorityList);
    }

    public Authentication authenticate(String username, String password){
        UserDetails userDetails = this.loadUserByUsername(username);

        if(userDetails == null)
            throw new BadCredentialsException("Nombre de usuario incorrecto");

        if(!passwordEncoder.matches(password, userDetails.getPassword()))
            throw new BadCredentialsException("Contraseña incorrecta");

        return new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
    }

    public Authentication authenticate(String username){
        UserDetails userDetails = this.loadUserByUsername(username);
        if(userDetails == null) {
            throw new BadCredentialsException("Nombre de usuario incorrecto");
        }

        return new UsernamePasswordAuthenticationToken(username, userDetails.getPassword(), userDetails.getAuthorities());
    }

    public AuthLoguinResponseDTO loguin (AuthLoguinRequestDTO authLoguinRequestDTO){
        String username = authLoguinRequestDTO.nombreUsuario();
        String password = authLoguinRequestDTO.contrasenia();

        Authentication authentication = this.authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);


        Usuario usuario = usuarioRepo.findUserEntityByusername((String) authentication.getPrincipal()).get();
        String role = Utili.obtenerRol(authentication);

        String accesToken = jwtUtil.createToken(authentication, 60 * 2000);

        String refreshToekn = jwtUtil.createRefreshToken(usuario.getUsername(), DuracionToken.REFRESH_TOKEN.getDuracion());
        actualizarRefreshToken(usuario.getRefreshToken(), refreshToekn);
        //usuario.setRefreshToken(refreshToekn);
        //usuario.getRefreshToken().setToken(refreshToekn);
        //refreshTokenService.update(usuario.getRefreshToken());


        AuthLoguinResponseDTO authLoguinResponseDTO = new AuthLoguinResponseDTO(username, role, usuario.getId(),"Loguin correcto", accesToken, true);
        return authLoguinResponseDTO;
    }

    public void actualizarRefreshToken(RefreshToken refreshToken, String nuevoToken){
        refreshToken.setToken(nuevoToken);
        refreshTokenService.update(refreshToken);
    }

}
