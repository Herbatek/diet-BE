package com.piotrek.diet.cart;

import com.piotrek.diet.meal.MealDtoConverter;
import com.piotrek.diet.product.ProductDtoConverter;
import com.piotrek.diet.sample.CartEquals;
import com.piotrek.diet.sample.CartSample;
import com.piotrek.diet.sample.MealSample;
import com.piotrek.diet.sample.ProductSample;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CartDtoConverterTest {

    private ProductDtoConverter productDtoConverter = new ProductDtoConverter();
    private MealDtoConverter mealDtoConverter = new MealDtoConverter(productDtoConverter);
    private CartDtoConverter cartDtoConverter = new CartDtoConverter(mealDtoConverter, productDtoConverter);

    private Cart cart;
    private CartDto cartDto;

    @BeforeEach
    void beforeEach() {
        cart = CartSample.cart1();
        cartDto = CartSample.cartDto1();
    }

    @Test
    void toDto() {
        final var converted = cartDtoConverter.toDto(cart);
        assertTrue(CartEquals.cartDtoEquals(cartDto, converted));
    }

    @Test
    void toDto_withMealsAndProducts() {
        addProducts();
        addMeals();

        final var converted = cartDtoConverter.toDto(cart);
        assertTrue(CartEquals.cartDtoEquals(cartDto, converted));
    }

    @Test
    void fromDto() {
        final Cart converted = cartDtoConverter.fromDto(cartDto);
        assertTrue(CartEquals.cartEquals(cart, converted));
    }

    @Test
    void fromDto_withMealsAndProducts() {
        addMeals();
        addProducts();

        final Cart converted = cartDtoConverter.fromDto(cartDto);
        assertTrue(CartEquals.cartEquals(cart, converted));
    }

    private void addProducts() {
        cart.getProducts().add(ProductSample.bananaWithId());
        cart.getProducts().add(ProductSample.breadWithId());

        cartDto.getProducts().add(ProductSample.bananaWithIdDto());
        cartDto.getProducts().add(ProductSample.breadWithIdDto());
        cartDto.setItemCounter(cartDto.getItemCounter() + 2);
        cartDto.getAllProducts().addAll(cartDto.getProducts());
    }

    private void addMeals() {
        cart.getMeals().add(MealSample.coffeeWithId());
        cart.getMeals().add(MealSample.dumplingsWithId());

        cartDto.getMeals().add(MealSample.coffeeWithIdDto());
        cartDto.getMeals().add(MealSample.dumplingsWithIdDto());
        cartDto.setItemCounter(cartDto.getItemCounter() + 2);
    }

}