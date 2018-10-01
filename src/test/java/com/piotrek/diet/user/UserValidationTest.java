package com.piotrek.diet.user;

import com.piotrek.diet.helpers.exceptions.BadRequestException;
import com.piotrek.diet.sample.UserSample;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

class UserValidationTest {

    private UserValidation userValidation = new UserValidation();
    private User user = UserSample.johnWithId();

    @Test
    void validateUserWithPrincipal_whenTheyHaveTheSameIt_thenDoNothing() {
        var testingAuthentication = new TestingAuthenticationToken(user.getId(), null);
        SecurityContextHolder.getContext().setAuthentication(testingAuthentication);

        userValidation.validateUserWithPrincipal(user.getId());
    }

    @Test
    void validateUserWithPrincipal_whenTheyHaveDifferent_thenThrowBadRequestException() {
        var testingAuthentication = new TestingAuthenticationToken(user.getId(), null);
        SecurityContextHolder.getContext().setAuthentication(testingAuthentication);

        Assertions.assertThrows(BadRequestException.class, () -> userValidation.validateUserWithPrincipal("badUserId"));
    }
}