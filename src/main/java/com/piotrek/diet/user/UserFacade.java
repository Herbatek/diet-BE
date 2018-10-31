package com.piotrek.diet.user;

import com.piotrek.diet.cart.Cart;
import com.piotrek.diet.cart.CartDto;
import com.piotrek.diet.cart.CartDtoConverter;
import com.piotrek.diet.cart.CartService;
import com.piotrek.diet.helpers.Page;
import com.piotrek.diet.meal.Meal;
import com.piotrek.diet.meal.MealDto;
import com.piotrek.diet.meal.MealDtoConverter;
import com.piotrek.diet.meal.MealService;
import com.piotrek.diet.product.Product;
import com.piotrek.diet.product.ProductDto;
import com.piotrek.diet.product.ProductDtoConverter;
import com.piotrek.diet.product.ProductService;
import com.piotrek.diet.security.token.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserFacade {

    private final CartService cartService;
    private final UserService userService;
    private final UserValidation userValidation;
    private final ProductDtoConverter productDtoConverter;
    private final ProductService productService;
    private final MealService mealService;
    private final MealDtoConverter mealDtoConverter;
    private final TokenService tokenService;
    private final CartDtoConverter cartDtoConverter;

    Mono<CartDto> findDtoCartByUserAndDate(String userId, LocalDate date) {
        return cartService.findByUserIdAndDate(userId, date).map(cartDtoConverter::toDto);
    }

    Mono<UserDto> findDtoUser(String userId) {
        return userService.findDtoById(userId);
    }

    Mono<UserDto> updateUser(String userId, UserDto update) {
        UserDto userDto = userService.update(userId, update).block();
        String tokenValue = tokenService.generateToken(userDto);
        tokenService.update(tokenValue, tokenService.findByUserId(userId).block().getId()).block();
        return Mono.just(userDto);
    }

    Mono<ProductDto> createProduct(String userId, ProductDto productDto) {
        userValidation.validateUserWithPrincipal(userId);
        return userService.findById(userId)
                .doOnNext(user -> productDto.setUserId(user.getId()))
                .map(user -> productDto)
                .flatMap(productService::save);
    }

    Mono<Page<ProductDto>> findAllProductsByUserId(String userId, Pageable pageable) {
        return userService.findById(userId)
                .then(productService
                        .findAllByUserPageable(userId, pageable));
    }

    Mono<MealDto> createMeal(String userId, MealDto mealDto) {
        userValidation.validateUserWithPrincipal(userId);
        return userService.findById(userId)
                .doOnNext(user -> mealDto.setUserId(user.getId()))
                .flatMap(user -> mealService.save(mealDto))
                .map(mealDtoConverter::toDto);
    }

    Mono<Page<MealDto>> findAllMealsByUser(String userId, Pageable pageable) {
        return userService.findById(userId).then(mealService
                .findAllByUserId(userId, pageable));
    }


    Mono<Page<MealDto>> findFavouriteMeals(String userId, Pageable pageable) {
        return userService.findById(userId)
                .map(User::getFavouriteMeals)
                .flatMap(meals -> Mono.just(new Page<>(meals
                        .stream()
                        .skip(pageable.getPageNumber() * pageable.getPageSize())
                        .limit(pageable.getPageSize())
                        .map(mealDtoConverter::toDto)
                        .collect(Collectors.toList()), pageable.getPageNumber(), pageable.getPageSize(), meals.size())));
    }

    Mono<Void> addToFavourite(String userId, String mealId) {
        userValidation.validateUserWithPrincipal(userId);
        return userService.findById(userId)
                .flatMap(user -> mealService.findById(mealId)
                        .doOnNext(meal -> user.getFavouriteMeals().add(meal))
                        .flatMap(meal -> userService.save(user)))
                .then();
    }

    Mono<Void> deleteFromFavourite(String userId, String mealId) {
        userValidation.validateUserWithPrincipal(userId);
        return userService.findById(userId)
                .doOnNext(user -> user.getFavouriteMeals().remove(new Meal(mealId)))
                .flatMap(userService::save)
                .then();
    }

    Mono<MealDto> findMealDtoById(String id) {
        return mealService.findById(id).map(mealDtoConverter::toDto);
    }

    Mono<Boolean> isFavourite(String userId, String mealId) {
        userValidation.validateUserWithPrincipal(userId);
        return userService.findById(userId)
                .map(user -> user.getFavouriteMeals().contains(new Meal(mealId)));
    }

    Mono<CartDto> addMealToCart(String userId, String mealId, LocalDate date, int amount) {
        Cart cart = cartService.findByUserIdAndDate(userId, date)
                .onErrorReturn(new Cart(userId, date)).block();
        userValidation.validateUserWithPrincipal(cart.getUserId());
        Meal meal = mealService.findById(mealId).block();
        if (cart.getMeals().contains(meal)) {
            int indexOfDuplicated = cart.getMeals().indexOf(meal);
            Meal duplicated = cart.getMeals().remove(indexOfDuplicated);
            amount += duplicated.getAmount();
        }

        double divider = (double) meal.getAmount() / amount;
        meal.getProducts()
                .forEach(productDto -> {
                    productDto.setAmount((int) (productDto.getAmount() / divider));
                    productDto.setProtein(productDto.getProtein() / divider);
                    productDto.setCarbohydrate(productDto.getCarbohydrate() / divider);
                    productDto.setFat(productDto.getFat() / divider);
                    productDto.setFibre(productDto.getFibre() / divider);
                    productDto.setProteinAndFatEquivalent(productDto.getProteinAndFatEquivalent() / divider);
                    productDto.setCarbohydrateExchange(productDto.getCarbohydrateExchange() / divider);
                    productDto.setKcal(productDto.getKcal() / divider);
                });
        mealService.calculateMealInformation(meal);
        meal.setAmount(amount);
        cart.getMeals().add(meal);
        return cartService.save(cart).map(cartDtoConverter::toDto);
    }

    Mono<CartDto> deleteMealFromCart(String userId, String mealId, LocalDate date) {
        userValidation.validateUserWithPrincipal(userId);
        return cartService.findByUserIdAndDate(userId, date)
                .flatMap(cart -> {
                    Meal meal = new Meal(mealId);
                    if (cart.getMeals().contains(meal)) {
                        cart.getMeals().remove(meal);
                        return cartService.save(cart).map(cartDtoConverter::toDto);
                    } else
                        return Mono.just(cart).map(cartDtoConverter::toDto);
                });
    }

    Mono<CartDto> addProductToCart(String userId, String productId, LocalDate date, int amount) {
        Cart cart = cartService.findByUserIdAndDate(userId, date)
                .onErrorReturn(new Cart(userId, date)).block();
        userValidation.validateUserWithPrincipal(cart.getUserId());
        Product product = productService.findById(productId).block();
        product.setAmount(amount);
        if (cart.getProducts().contains(product)) {
            int indexOfDuplicated = cart.getProducts().indexOf(product);
            var duplicated = cart.getProducts().remove(indexOfDuplicated);
            product.setAmount(product.getAmount() + duplicated.getAmount());
        }
        cart.getProducts().add(product);
        return cartService.save(cart).map(cartDtoConverter::toDto);
    }

    Mono<CartDto> deleteProductFromCart(String userId, String productId, LocalDate date) {
        Cart cart = cartService.findByUserIdAndDate(userId, date).block();
        userValidation.validateUserWithPrincipal(cart.getUserId());
        Product product = productService.findById(productId).block();
        if (cart.getProducts().contains(product)) {
            cart.getProducts().remove(product);
            cart = cartService.save(cart).block();
        }
        return Mono.just(cart).map(cartDtoConverter::toDto);
    }

}
