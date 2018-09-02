package com.piotrek.diet.meal;

import com.piotrek.diet.helpers.DtoConverter;
import org.springframework.stereotype.Component;

@Component
public class MealDtoConverter implements DtoConverter<Meal, MealDto> {

    @Override
    public MealDto toDto(Meal meal) {
        var mealDto = new MealDto();

        mealDto.setId(meal.getId());
        mealDto.setName(meal.getName());
        mealDto.setDescription(meal.getDescription());
        mealDto.setRecipe(meal.getRecipe());
        mealDto.setProtein(meal.getProtein());
        mealDto.setCarbohydrate(meal.getCarbohydrate());
        mealDto.setFat(meal.getFat());
        mealDto.setFibre(meal.getFibre());
        mealDto.setKcal(meal.getKcal());
        mealDto.setImageUrl(meal.getImageUrl());
        mealDto.setCarbohydrateExchange(meal.getCarbohydrateExchange());
        mealDto.setProteinAndFatEquivalent(meal.getProteinAndFatEquivalent());

        return mealDto;
    }

    @Override
    public Meal fromDto(MealDto mealDto) {
        var meal = new Meal();

        meal.setId(mealDto.getId());
        meal.setName(mealDto.getName());
        meal.setDescription(mealDto.getDescription());
        meal.setRecipe(mealDto.getRecipe());
        meal.setProtein(mealDto.getProtein());
        meal.setCarbohydrate(mealDto.getCarbohydrate());
        meal.setFat(mealDto.getFat());
        meal.setFibre(mealDto.getFibre());
        meal.setKcal(mealDto.getKcal());
        meal.setImageUrl(mealDto.getImageUrl());
        meal.setCarbohydrateExchange(mealDto.getCarbohydrateExchange());
        meal.setProteinAndFatEquivalent(mealDto.getProteinAndFatEquivalent());

        return meal;
    }
}
