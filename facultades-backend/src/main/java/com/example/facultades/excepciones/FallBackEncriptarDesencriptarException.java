package com.example.facultades.excepciones;

public class FallBackEncriptarDesencriptarException extends RuntimeException{
    public FallBackEncriptarDesencriptarException(){super("El servicio de encriptación no esta disponible, por lo cual no es posible enviar mensajes directos");}
}
