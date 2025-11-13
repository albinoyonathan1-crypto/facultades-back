package com.example.facultades.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"username", "message","jwt", "status"})
public record AuthLoguinResponseDTO(String nombre,
                                    String role,
                                    Long id,
                                    String mensaje,
                                    String token,
                                    //String refreshToken,
                                    Boolean status) {
}
