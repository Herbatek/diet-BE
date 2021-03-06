package com.piotrek.diet.cart;

import com.piotrek.diet.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.piotrek.diet.helpers.Constants.DATE_FORMAT;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartDtoConverter cartDtoConverter;

    public Mono<Cart> findByUserIdAndDate(String userId, LocalDate localDate) {
        final var EXCEPTION_MESSAGE = "Not found cart for user [id = " + userId + " and date: " +
                localDate.format(DateTimeFormatter.ofPattern(DATE_FORMAT)) + "]";

        return cartRepository.findByUserIdAndDate(userId, localDate)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException(EXCEPTION_MESSAGE))));
    }

    public Mono<Cart> save(Cart cart) {
        return cartRepository.save(cart);
    }

    public Mono<Cart> save(CartDto cartDto) {
        return save(cartDtoConverter.fromDto(cartDto));
    }

    public Mono<Void> deleteAll() {
        return cartRepository.deleteAll();
    }

}
