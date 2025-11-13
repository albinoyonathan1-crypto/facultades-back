package com.example.facultades.excepciones;

public class RespuestaNoEncontradaException extends RuntimeException{
    public RespuestaNoEncontradaException(){
        super("No se ha podido encontrar la respusta");
    }
}
