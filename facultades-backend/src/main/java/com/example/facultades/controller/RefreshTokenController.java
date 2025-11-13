package com.example.facultades.controller;

import com.example.facultades.dto.RefreshTokenDto;
import com.example.facultades.generics.ControllerGeneric;
import com.example.facultades.model.RefreshToken;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/refreshToken")
public class RefreshTokenController extends ControllerGeneric<RefreshToken, RefreshTokenDto,Long> {
}
