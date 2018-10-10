package com.piotrek.diet.user;

import com.piotrek.diet.helpers.DtoConverter;
import org.springframework.stereotype.Component;

@Component
public class UserDtoConverter implements DtoConverter<User, UserDto> {

    @Override
    public UserDto toDto(User user) {
        var userDto = new UserDto();
        userDto.setUsername(user.getUsername());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setPicture_url(user.getPictureUrl());
        userDto.setAge(user.getAge());
        userDto.setHeight(user.getHeight());
        userDto.setWeight(user.getWeight());
        return userDto;
    }

    @Override
    public User fromDto(UserDto dto) {
        var user = new User();
        user.setUsername(dto.getUsername());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setId(dto.getId());
        user.setEmail(dto.getEmail());
        user.setPictureUrl(dto.getPicture_url());
        user.setAge(dto.getAge());
        user.setHeight(dto.getHeight());
        user.setWeight(dto.getWeight());
        return user;
    }
}
