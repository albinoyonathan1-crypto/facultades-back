package com.example.facultades.excepciones;

public class NickEnUsoException extends RuntimeException{
    public NickEnUsoException(){
        super("El nick ya está en uso");
    }
}
