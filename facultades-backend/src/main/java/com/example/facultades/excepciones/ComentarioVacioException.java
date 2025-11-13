package com.example.facultades.excepciones;

public class ComentarioVacioException extends RuntimeException{
    public ComentarioVacioException(){
        super("No se permiten comentarios en blanco");
    }
}
