package com.example.facultades.excepciones;

public class RegistroExistenteException extends  Exception{
    public RegistroExistenteException(String mensaje){
        super(mensaje);
    }
}
