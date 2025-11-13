package com.example.facultades.dto;

import com.example.facultades.model.RefreshToken;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class RefreshTokenDto extends BaseDTO<RefreshToken> {

    private String token;
}
