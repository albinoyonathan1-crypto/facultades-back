package com.example.facultades.excepciones;

public class ComentarioEliminadoException extends RuntimeException{
    public ComentarioEliminadoException(){
        super("El comentario fue eliminado");
    }
}
