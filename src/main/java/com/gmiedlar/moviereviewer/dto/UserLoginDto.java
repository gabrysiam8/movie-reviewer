package com.gmiedlar.moviereviewer.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class UserLoginDto {

    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
