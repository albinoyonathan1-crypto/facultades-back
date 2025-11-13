package com.example.facultades.excepciones;

public class EmailNoEncontradoException extends  Exception{
    public EmailNoEncontradoException(){
        super("El email ingresado no existe en la base de datos");
    }
}
