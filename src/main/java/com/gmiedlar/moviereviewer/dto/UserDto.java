package com.gmiedlar.moviereviewer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private String email;

    private String username;

    private int moviesAdded;
}
