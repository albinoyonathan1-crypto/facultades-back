package com.example.facultades.excepciones;

public class UsuarioNoEncontradoException extends RuntimeException{

    public UsuarioNoEncontradoException(){
        super("El usuario que ingresaste no exite en la base de datos");
    }

    public UsuarioNoEncontradoException(String mensaje){
        super(mensaje);
    }

}
