package com.example.facultades.service;

import com.example.facultades.dto.BaseDTO;
import com.example.facultades.dto.RefreshTokenDto;
import com.example.facultades.generics.GenericService;
import com.example.facultades.model.RefreshToken;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenService extends GenericService<RefreshToken, Long> implements IRefreshTokenService{

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public BaseDTO<RefreshToken> convertirDTO(RefreshToken refreshToken) {
        return modelMapper.map(refreshToken, RefreshTokenDto.class);
    }

    @Override
    public RefreshToken converirEntidad(BaseDTO<RefreshToken> DTO) {
        return modelMapper.map(DTO, RefreshToken.class);
    }
}
