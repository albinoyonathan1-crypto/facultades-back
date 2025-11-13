package com.example.facultades.service;


import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.facultades.enums.DuracionToken;
import com.example.facultades.model.RefreshToken;
import com.example.facultades.util.JwtUtil;
import com.example.facultades.model.OidcUserPersonalizado;
import com.example.facultades.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
public class CustomOidcUserService extends OidcUserService {

    @Autowired
    private IUsuarioService usuarioUservice;

    @Autowired
    @Lazy
    private UserDetailsServiceImp userDetails;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JwtUtil jwtUtil;


    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);
        String userName = oidcUser.getEmail();
        String nick = oidcUser.getGivenName();
        String imagenPerfil = oidcUser.getPicture();
        // Obtener o crear el usuario en la base de datos
        Usuario usuario = usuarioUservice.findUserEntityByusername(userName)
                .orElseGet(() -> usuarioUservice.saveUserOauth(userName, nick, imagenPerfil));

        // Autenticar al usuario
        Authentication authentication = authenticateUser(usuario.getUsername());

        // Generar tokens
        String jwtToken = generateJwtToken(authentication);
        String refreshToken = refreshToken(usuario);

        // Obtener el rol más adecuado
        String role = getRole(authentication);

        // Retornar el usuario OIDC personalizado con los tokens y roles
        return new OidcUserPersonalizado(oidcUser, jwtToken, role, usuario.getId());
    }


    // Método para autenticar al usuario
    private Authentication authenticateUser(String username) {
        return userDetails.authenticate(username);
    }

    // Método para generar JWT token
    private String generateJwtToken(Authentication authentication) {
        return jwtUtil.createToken(authentication, 2 * 60000);  // Tiempo ajustable según sea necesario
    }

    // Método para manejar el refresh token
    private String refreshToken(Usuario usuario) {
        String refreshToken = null;
        if (usuario.getRefreshToken() != null) {
            refreshToken = usuario.getRefreshToken().getToken();
        }

        if (refreshToken != null) {
            try {
                jwtUtil.validateToken(refreshToken);
            } catch (JWTVerificationException exception) {
                // Si el refresh token es inválido o ha expirado, crear uno nuevo
                refreshToken = jwtUtil.createRefreshToken(usuario.getUsername(), DuracionToken.REFRESH_TOKEN.getDuracion());
                actualizarRefreshToken(usuario.getRefreshToken(), refreshToken);
            }
        } else {
            // Si no hay refresh token, crear uno nuevo
            refreshToken = jwtUtil.createRefreshToken(usuario.getUsername(), DuracionToken.REFRESH_TOKEN.getDuracion());
        }

        return refreshToken;
    }

    // Método para obtener el rol del usuario
    private String getRole(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(auth -> auth.equals("ROLE_ADMIN") || auth.equals("ROLE_USER"))
                .findFirst()
                .orElse("ROLE_USER");
    }



    public void actualizarRefreshToken(RefreshToken refreshToken, String nuevoToken){
        refreshToken.setToken(nuevoToken);
        refreshTokenService.update(refreshToken);
    }

}
