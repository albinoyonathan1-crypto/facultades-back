package com.example.facultades.excepciones;

public class CaptchaException extends RuntimeException{
    public CaptchaException(){
        super("Has fallado en el captcha. Por favor, inténtalo de nuevo.");
    }
}
