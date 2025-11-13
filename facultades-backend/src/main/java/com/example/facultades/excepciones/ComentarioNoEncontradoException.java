package com.example.facultades.excepciones;

public class ComentarioNoEncontradoException extends RuntimeException{
    public ComentarioNoEncontradoException(){
        super("No se ha podido encontrar el comentario de origen");
    }
}
