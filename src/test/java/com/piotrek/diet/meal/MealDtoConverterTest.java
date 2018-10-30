package com.piotrek.diet.meal;

import com.piotrek.diet.product.ProductDtoConverter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.Arrays;

import static com.piotrek.diet.helpers.MealSample.*;
import static com.piotrek.diet.helpers.ProductSample.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MealDtoConverterTest {

    private MealDtoConverter mealDtoConverter;

    private Meal meal;
    private MealDto mealDto;

    @BeforeAll
    void setup() {
        mealDtoConverter = new MealDtoConverter(new ProductDtoConverter());

        meal = dumplingsWithId();
        meal.setProducts(new ArrayList<>(Arrays.asList(breadWithId(), bananaWithId())));

        mealDto = dumplingsWithIdDto();
        mealDto.setProducts(new ArrayList<>(Arrays.asList(breadWithIdDto(), bananaWithIdDto())));
    }

    @Test
    @DisplayName("Convert entity meal to dto")
    void toDto() {
        final var convertedMeal = mealDtoConverter.toDto(meal);
        this.assertEqualMealDtoAllFields(mealDto, convertedMeal);
    }

    @Test
    @DisplayName("Convert dto to meal entity")
    void fromDto() {
        final var convertedMeal = mealDtoConverter.fromDto(mealDto);
        this.assertEqualMealAllFields(meal, convertedMeal);
    }

    @Test
    @DisplayName("Convert meal list to dto list")
    void listToDto() {
        final var beforeConvert = new ArrayList<>(Arrays.asList(dumplingsWithId(), coffeeWithId()));
        final var afterConvert = mealDtoConverter.listToDto(beforeConvert);
        final var expected = new ArrayList<>(Arrays.asList(dumplingsWithIdDto(), coffeeWithIdDto()));

        assertAll(
                () -> this.assertEqualMealDtoAllFields(expected.get(0), afterConvert.get(0)),
                () -> this.assertEqualMealDtoAllFields(expected.get(1), afterConvert.get(1))
        );
    }

    @Test
    @DisplayName("Convert entity meal list from dto list")
    void listFromDto() {
        final var beforeConvert = new ArrayList<>(Arrays.asList(dumplingsWithIdDto(), coffeeWithIdDto()));
        final var afterConvert = mealDtoConverter.listFromDto(beforeConvert);
        final var expected = new ArrayList<>(Arrays.asList(dumplingsWithId(), coffeeWithId()));

        assertAll(
                () -> this.assertEqualMealAllFields(expected.get(0), afterConvert.get(0)),
                () -> this.assertEqualMealAllFields(expected.get(1), afterConvert.get(1))
        );
    }

    private void assertEqualMealAllFields(Meal expected, Meal actual) {
        assertNotNull(actual);
        assertAll(
                () -> assertEquals(expected.getId(), actual.getId()),
                () -> assertEquals(expected.getName(), actual.getName()),
                () -> assertEquals(expected.getDescription(), actual.getDescription()),
                () -> assertEquals(expected.getRecipe(), actual.getRecipe()),
                () -> assertEquals(expected.getProtein(), actual.getProtein()),
                () -> assertEquals(expected.getCarbohydrate(), actual.getCarbohydrate()),
                () -> assertEquals(expected.getFat(), actual.getFat()),
                () -> assertEquals(expected.getFibre(), actual.getFibre()),
                () -> assertEquals(expected.getKcal(), actual.getKcal()),
                () -> assertEquals(expected.getAmount(), actual.getAmount()),
                () -> assertEquals(expected.getImageUrl(), actual.getImageUrl()),
                () -> assertEquals(expected.getCarbohydrateExchange(), actual.getCarbohydrateExchange()),
                () -> assertEquals(expected.getProteinAndFatEquivalent(), actual.getProteinAndFatEquivalent()),
                () -> assertEquals(expected.getProducts().size(), actual.getProducts().size()),
                () -> assertEquals(expected.getUserId(), actual.getUserId())
        );
    }

    private void assertEqualMealDtoAllFields(MealDto expected, MealDto actual) {
        assertNotNull(actual);
        assertAll(
                () -> assertEquals(expected.getId(), actual.getId()),
                () -> assertEquals(expected.getName(), actual.getName()),
                () -> assertEquals(expected.getDescription(), actual.getDescription()),
                () -> assertEquals(expected.getRecipe(), actual.getRecipe()),
                () -> assertEquals(expected.getProtein(), actual.getProtein()),
                () -> assertEquals(expected.getCarbohydrate(), actual.getCarbohydrate()),
                () -> assertEquals(expected.getFat(), actual.getFat()),
                () -> assertEquals(expected.getFibre(), actual.getFibre()),
                () -> assertEquals(expected.getKcal(), actual.getKcal()),
                () -> assertEquals(expected.getAmount(), actual.getAmount()),
                () -> assertEquals(expected.getImageUrl(), actual.getImageUrl()),
                () -> assertEquals(expected.getCarbohydrateExchange(), actual.getCarbohydrateExchange()),
                () -> assertEquals(expected.getProteinAndFatEquivalent(), actual.getProteinAndFatEquivalent()),
                () -> assertEquals(expected.getProducts().size(), actual.getProducts().size()),
                () -> assertEquals(expected.getUserId(), actual.getUserId())
        );
    }
}