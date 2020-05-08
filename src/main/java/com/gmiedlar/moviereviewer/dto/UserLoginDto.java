package com.gmiedlar.moviereviewer.dto;

import javax.validation.constraints.NotBlank;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserLoginDto {

    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
