package com.example.facultades.excepciones;

public class ContrasenaNoEncontradaException extends Exception{
    public ContrasenaNoEncontradaException(){
        super("La contraseña ingresada es incorrecta");
    }
}
