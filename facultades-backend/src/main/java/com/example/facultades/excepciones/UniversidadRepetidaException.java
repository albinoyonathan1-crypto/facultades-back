package com.example.facultades.excepciones;

public class UniversidadRepetidaException extends Exception{
    public UniversidadRepetidaException(){
        super("La universidad que deseas ingresar ya existe");
    }
}
