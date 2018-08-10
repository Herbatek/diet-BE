package com.piotrek.diet.model.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class UserDto {

    @NotNull
    private String id;

    private String username;

    @Email
    private String email;

    private String firstName;
    private String lastName;
    private String picture_url;
    private String role;
}