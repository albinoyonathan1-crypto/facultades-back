package com.example.facultades.excepciones;

public class RespuestaVaciaException extends RuntimeException{
   public RespuestaVaciaException(){
       super("No se permiten respuestas en blanco");
   }
}
