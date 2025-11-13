package com.example.facultades.service;

import com.example.facultades.dto.RecaptchaResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

@Service
public class RecaptchaService {
   @Value("${CAPTCHA_SECRET_KEY}")
    private String secretKey;;// = "6LeO030qAAAAAJCurA34yr-H0rclxgFk63qqyrh5";

    private static final String RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    // Método que verifica el token de reCAPTCHA
    public boolean verifyRecaptcha(String token) {
        // Construir la URL para hacer la solicitud a la API de Google
        String url = RECAPTCHA_VERIFY_URL+"?secret="+secretKey+"&response="+token;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<RecaptchaResponse> response = restTemplate.postForEntity(url, null, RecaptchaResponse.class);

        // Si la respuesta es exitosa y el campo "success" es true, el token es válido
        return response.getBody() != null && response.getBody().success();
    }
}
