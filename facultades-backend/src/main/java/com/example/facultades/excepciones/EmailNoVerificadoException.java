package com.example.facultades.excepciones;

public class EmailNoVerificadoException extends RuntimeException{
    public EmailNoVerificadoException (){
        super("El email aún no ha sido verificado");
    }
}
