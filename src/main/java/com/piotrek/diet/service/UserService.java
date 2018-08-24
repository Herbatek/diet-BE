package com.piotrek.diet.service;

import com.piotrek.diet.exception.NotFoundException;
import com.piotrek.diet.model.User;
import com.piotrek.diet.model.enumeration.Role;
import com.piotrek.diet.repository.UserRepository;
import com.piotrek.diet.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserValidator userValidator;

    public Mono<User> findById(String id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Not found user [id = " + id + "]"))));
    }

    public Mono<User> findByFacebookId(Long facebookId) {
        return userRepository.findByFacebookId(facebookId);
    }

    public Flux<User> findAll() {
        return userRepository.findAll();
    }

    public Mono<User> save(User user) {
        return userRepository.save(user);
    }

    public Mono<Void> deleteAll() {
        return userRepository.deleteAll();
    }

    public User createUser(Long facebookId, String email, String firstName, String lastName) {
        userValidator.checkFacebookId(facebookId);
        var user = new User();
        user.setFacebookId(facebookId);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole(Role.ROLE_USER.name());
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }
}
