package com.piotrek.diet.user;

import com.piotrek.diet.helpers.UserSample;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.piotrek.diet.helpers.AssertEqualAllFields.assertUserFields;

class UserDtoConverterTest {

    private UserDtoConverter userDtoConverter = new UserDtoConverter();

    private User user;
    private UserDto userDto;

    @BeforeEach
    void beforeEach() {
        user = UserSample.john();
        userDto = UserSample.johnDto();
    }

    @Test
    void toDto() {
        var convertedUser = userDtoConverter.toDto(user);
        assertUserFields(userDto, convertedUser);
    }

    @Test
    void fromDto() {
        user.setFacebookId(0);
        user.setLastVisit(null);
        user.setRole(null);
        var convertedUser = userDtoConverter.fromDto(userDto);
        assertUserFields(user, convertedUser);
    }
}