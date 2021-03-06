package com.piotrek.diet.user;

import com.piotrek.diet.cart.CartDto;
import com.piotrek.diet.cart.CartFacade;
import com.piotrek.diet.helpers.Page;
import com.piotrek.diet.meal.MealDto;
import com.piotrek.diet.product.ProductDto;
import com.piotrek.diet.security.token.Token;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.time.LocalDate;

import static com.piotrek.diet.helpers.Constants.DATE_FORMAT;
import static com.piotrek.diet.helpers.Page.DEFAULT_PAGE_SIZE;
import static com.piotrek.diet.helpers.Page.FIRST_PAGE_NUM;
import static com.piotrek.diet.security.helpers.SecurityConstants.COOKIE_MAX_AGE;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserFacade userFacade;
    private final CartFacade cartFacade;

    @GetMapping("/{id}")
    Mono<UserDto> findUserById(@PathVariable String id) {
        return userFacade.findDtoUser(id);
    }

    @PutMapping("/{id}")
    Mono<UserDto> updateUser(@PathVariable String id, @RequestBody @Valid UserDto userDto, HttpServletResponse response) {
        UserDto updated = userFacade.updateUser(id, userDto).block();
        Token token = userFacade.findToken(updated.getId()).block();

        Cookie cookie = new Cookie("Token", token.getToken());
        cookie.setMaxAge(COOKIE_MAX_AGE);
        cookie.setPath("/");
        response.addCookie(cookie);
        return Mono.just(updated);
    }

    @GetMapping("/{id}/products")
    Mono<Page<ProductDto>> findUserProducts(
            @PathVariable String id,
            @RequestParam(defaultValue = FIRST_PAGE_NUM) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size) {
        return userFacade.findAllProductsByUserId(id, PageRequest.of(page, size));
    }

    @GetMapping("/{id}/meals")
    Mono<Page<MealDto>> findUserMeals(
            @PathVariable String id,
            @RequestParam(defaultValue = FIRST_PAGE_NUM) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size) {
        return userFacade.findAllMealsByUser(id, PageRequest.of(page, size));
    }

    @GetMapping("/{id}/carts")
    Mono<CartDto> findUserCart(@PathVariable String id, @RequestParam @DateTimeFormat(pattern = DATE_FORMAT) LocalDate date) {
        return cartFacade.findDtoCartByUserAndDate(id, date);
    }

    @PostMapping("/{userId}/products")
    @ResponseStatus(CREATED)
    Mono<ProductDto> createProduct(@PathVariable String userId, @Valid ProductDto productDto) {
        return userFacade.createProduct(userId, productDto);
    }

    @PostMapping("/{userId}/meals")
    @ResponseStatus(CREATED)
    Mono<MealDto> createMeal(@PathVariable String userId, @Valid MealDto mealDto){
        return userFacade.createMeal(userId, mealDto);
    }

    @GetMapping("/{id}/meals/favourites")
    Mono<Page<MealDto>> getFavouriteMeals(
            @PathVariable String id,
            @RequestParam(defaultValue = FIRST_PAGE_NUM) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size) {
        return userFacade.findFavouriteMeals(id, PageRequest.of(page, size));
    }

    @PostMapping("/{userId}/meals/{mealId}/favourites")
    @ResponseStatus(CREATED)
    Mono<Void> addMealToFavourite(@PathVariable String userId, @PathVariable String mealId) {
        return userFacade.addToFavourite(userId, mealId);
    }

    @DeleteMapping("/{userId}/meals/{mealId}/favourites")
    @ResponseStatus(NO_CONTENT)
    Mono<Void> deleteMealFromFavourite(@PathVariable String userId, @PathVariable String mealId) {
        return userFacade.deleteFromFavourite(userId, mealId);
    }

    @GetMapping("/{userId}/meals/{mealId}/favourites")
    Mono<Boolean> isFavouriteMeal(@PathVariable String userId, @PathVariable String mealId) {
        return userFacade.isFavourite(userId, mealId);
    }

    @PostMapping("/{userId}/carts/meals/{mealId}")
    Mono<CartDto> addMealToCart(@PathVariable String userId, @PathVariable String mealId,
                                @RequestParam @DateTimeFormat(pattern = DATE_FORMAT) LocalDate date,
                                @RequestParam int amount) {
        return cartFacade.addMealToCart(userId, mealId, date, amount);
    }

    @PostMapping("/{userId}/carts/products/{productId}")
    Mono<CartDto> addProductToCart(@PathVariable String userId, @PathVariable String productId,
                                   @RequestParam @DateTimeFormat(pattern = DATE_FORMAT) LocalDate date,
                                   @RequestParam int amount) {
        return cartFacade.addProductToCart(userId, productId, date, amount);
    }

    @DeleteMapping("/{userId}/carts/meals/{mealId}")
    Mono<CartDto> deleteMealFromCart(@PathVariable String userId, @PathVariable String mealId,
                                     @RequestParam @DateTimeFormat(pattern = DATE_FORMAT) LocalDate date) {
        return cartFacade.deleteMealFromCart(userId, mealId, date);
    }

    @DeleteMapping("/{userId}/carts/products/{productId}")
    Mono<CartDto> deleteProductFromCart(@PathVariable String userId, @PathVariable String productId,
                                        @RequestParam @DateTimeFormat(pattern = DATE_FORMAT) LocalDate date) {
        return cartFacade.deleteProductFromCart(userId, productId, date);
    }
}