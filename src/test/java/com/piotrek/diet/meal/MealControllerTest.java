package com.piotrek.diet.meal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.piotrek.diet.DietApplication;
import com.piotrek.diet.helpers.Page;
import com.piotrek.diet.helpers.PrincipalProvider;
import com.piotrek.diet.helpers.config.DataBaseForIntegrationTestsConfiguration;
import com.piotrek.diet.helpers.exceptions.GlobalExceptionHandler;
import com.piotrek.diet.helpers.exceptions.NotFoundException;
import com.piotrek.diet.product.ProductDto;
import com.piotrek.diet.helpers.ProductEquals;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.piotrek.diet.helpers.MealSample.*;
import static com.piotrek.diet.helpers.ProductSample.bananaWithIdDto;
import static com.piotrek.diet.helpers.ProductSample.breadWithIdDto;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {DietApplication.class, DataBaseForIntegrationTestsConfiguration.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MealControllerTest {

    @Autowired
    private MealService mealService;

    @Autowired
    private MealDtoConverter mealDtoConverter;

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    private WebTestClient webTestClient;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Meal meal1;
    private Meal meal2;
    private MealDto mealDto1;
    private MealDto mealDto2;

    @BeforeEach
    void beforeEach() {
        mealService.deleteAll().block();
        createMeals();
        webTestClient = WebTestClient
                .bindToController(new MealController(mealService))
                .controllerAdvice(globalExceptionHandler)
                .build();
    }

    @AfterAll
    void afterAll() {
        mealService.deleteAll().block();
    }

    @Test
    @DisplayName("Find all without any parameters and when there are no meals, then return empty page")
    void findAll_whenDefaultParamsTotalElements0_thenReturnPageWithEmptyList() throws JsonProcessingException {
        final var URI = "/meals";
        final var expected = new Page<MealDto>(new ArrayList<>(), 0, 10, 0);

        mealService.deleteAll().block();

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    @DisplayName("Find all without any parameters and there are two meals, then return page with two meals")
    void findAll_whenDefaultParamsTotalElements2_thenReturnPageSupportWith2Meals() throws JsonProcessingException {
        final var URI = "/meals";
        final var expected = new Page<>(new ArrayList<>(Arrays.asList(mealDto1, mealDto2)), 0, 10, 2);

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    @DisplayName("When getAll page = 1, pageSize = 1 and there are 2 meals, then should be returned second page(because first page = 0) with one meal")
    void findAll_whenPageNumber1PageSize1TotalElements2_returnSecondPageWithOneMeal() throws JsonProcessingException {
        final var URI = "/meals?page=1&size=1";
        final var expected = new Page<>(new ArrayList<>(Arrays.asList(mealDto1, mealDto2))
                .stream()
                .skip(1)
                .limit(1)
                .collect(Collectors.toList()), 1, 1, 2);

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    @DisplayName("When findMealDtoById finds a single meal, meal will be returned as dto")
    void findById_whenFound_thenReturnMeal() throws JsonProcessingException {
        final var URI = "/meals/" + meal1.getId();

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(mealDto1));
    }

    @Test
    @DisplayName("When findMealDtoById doesn't find a meal, throws NotFoundException")
    void findById_whenNotFound_thenThrowNotFoundException() {
        final var URI = "/meals/aBadId";

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(NotFoundException.class);
    }

    @Test
    @DisplayName("Search meals, when there is two meals, but query matches for one of them, one of them will be returned in page wrapper")
    void searchByName_when2MealsAndQueryOneOfThem_thenReturnPageWithOneMeal() throws JsonProcessingException {
        final var URI = "/meals/search?query=" + meal1.getName();
        final var expected = new Page<>(List.of(mealDtoConverter.toDto(meal1)), 0, 10, 1);

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    @DisplayName("Search meals by name, when no query, then all products will be returned in page wrapper")
    void searchByName_when2MealsAndNoQuery_thenReturnPageWithAllMeals() throws JsonProcessingException {
        final var URI = "/meals/search";
        final var expected = new Page<>(new ArrayList<>(Arrays.asList(meal1, meal2))
                .stream()
                .map(mealDtoConverter::toDto)
                .collect(Collectors.toCollection(ArrayList::new)), 0, 10, 2);

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    @DisplayName("Search meals by name, when no query and no meals, will be returned empty list wrapped in page")
    void searchByName_whenNoMealsAndNoQuery_thenReturnEmptyPage() throws JsonProcessingException {
        final var URI = "/meals/search";
        final var expected = new Page<>(new ArrayList<>(), 0, 10, 0);

        mealService.deleteAll().block();

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    @DisplayName("Search meals, when query doesn't match any meal, then will be returned empty list wrapped in page")
    void searchByName_when2MealsAndQueryDoesNotMatch_thenReturnEmptyPage() throws JsonProcessingException {
        final var URI = "/meals/search?query=lol123";
        final var expected = new Page<>(new ArrayList<>(), 0, 10, 0);

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    @DisplayName("Search meals, when there are two products but query matches only one of them, then return page with one meal")
    void searchByName_when2MealsAndQueryCapitalCaseMatchOnlyOneOfThem_thenReturnMatchMealInPage() throws JsonProcessingException {
        final var URI = "/meals/search?query=" + meal1.getName().toUpperCase();
        final var expected = new Page<>(new ArrayList<>(Collections.singletonList(mealDto1)), 0, 10, 1);

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    @DisplayName("Search meals, when there are 2 meals, no query and pageSize = 1, then return first page with one meal")
    void searchByName_when2MealsAndNoQueryAndPageSize1_thenReturnFirstPageWithOneMeal() throws JsonProcessingException {
        final var URI = "/meals/search?size=1";
        final var expected = new Page<>(List.of(meal1), 0, 1, 2);

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(expected));
    }

    @Test
    @DisplayName("Update meal, then meal should be updated")
    void updateMeal_whenUpdateMeal_thenMealShouldBeUpdated() {
        final var URI = "/meals/" + meal1.getId();
        final var productDtos = new ArrayList<ProductDto>(2);

        final var firstProductInUpdate = bananaWithIdDto();
        firstProductInUpdate.setAmount(100);
        firstProductInUpdate.setUserId("someId");
        productDtos.add(firstProductInUpdate);

        final var secondProductInUpdate = breadWithIdDto();
        secondProductInUpdate.setAmount(30);
        secondProductInUpdate.setUserId("id");
        productDtos.add(secondProductInUpdate);

        PrincipalProvider.provide(meal1.getUserId());

        final MealDto update = dumplingsWithIdDto();
        update.setName("updated name");
        update.setRecipe("updated recipe");
        update.setDescription("updated description");
        update.setImageUrl("new image");
        update.setProducts(productDtos);

        MealDto responseBody = webTestClient.put().uri(URI)
                .body(BodyInserters.fromObject(update))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(MealDto.class)
                .returnResult()
                .getResponseBody();


        final MealDto expected = dumplingsWithIdDto();
        expected.setName("updated name");
        expected.setRecipe("updated recipe");
        expected.setDescription("updated description");
        expected.setImageUrl("new image");
        expected.setProducts(productDtos);
        expected.setAmount(firstProductInUpdate.getAmount() + secondProductInUpdate.getAmount());
        expected.setProtein(firstProductInUpdate.getProtein() + secondProductInUpdate.getProtein() * 0.3);
        expected.setProteinAndFatEquivalent(firstProductInUpdate.getProteinAndFatEquivalent() + secondProductInUpdate.getProteinAndFatEquivalent() * 0.3);
        expected.setFat(firstProductInUpdate.getFat() + secondProductInUpdate.getFat() * 0.3);
        expected.setFibre(firstProductInUpdate.getFibre() + secondProductInUpdate.getFibre() * 0.3);
        expected.setCarbohydrate(firstProductInUpdate.getCarbohydrate() + secondProductInUpdate.getCarbohydrate() * 0.3);
        expected.setCarbohydrateExchange(firstProductInUpdate.getCarbohydrateExchange() + secondProductInUpdate.getCarbohydrateExchange() * 0.3);
        expected.setKcal(firstProductInUpdate.getKcal() + secondProductInUpdate.getKcal() * 0.3);

        final var expectedSecondProduct = expected.getProducts().get(1);
        expectedSecondProduct.setKcal(expectedSecondProduct.getKcal() * 0.3);
        expectedSecondProduct.setProteinAndFatEquivalent(expectedSecondProduct.getProteinAndFatEquivalent() * 0.3);
        expectedSecondProduct.setProtein(expectedSecondProduct.getProtein() * 0.3);
        expectedSecondProduct.setFibre(expectedSecondProduct.getFibre() * 0.3);
        expectedSecondProduct.setFat(expectedSecondProduct.getFat() * 0.3);
        expectedSecondProduct.setCarbohydrate(expectedSecondProduct.getCarbohydrate() * 0.3);
        expectedSecondProduct.setCarbohydrateExchange(expectedSecondProduct.getCarbohydrateExchange() * 0.3);


        assertNotNull(responseBody);
        assertAll(
                () -> assertEquals(expected, responseBody),
                () -> assertTrue(ProductEquals.productDtoEquals(expected.getProducts().get(0), responseBody.getProducts().get(0))),
                () -> assertTrue(ProductEquals.productDtoEquals(expected.getProducts().get(1), responseBody.getProducts().get(1)))
        );
    }

    @Test
    @DisplayName("When update meal, and there is missing name, then throw BadRequestException")
    void updateMeal_whenUpdateHasNoName_throwBadRequestException() throws JsonProcessingException {
        final var URI = "/meals/" + meal1.getId();

        final var update = dumplingsWithIdDto();
        update.setName(null);

        webTestClient.put().uri(URI)
                .body(BodyInserters.fromObject(update))
                .exchange()
                .expectStatus().isBadRequest();

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(meal1));
    }

    @Test
    @DisplayName("When update meal, and there is missing recipe, then throw BadRequestException")
    void updateMeal_whenUpdateHasNoRecipe_throwBadRequestException() throws JsonProcessingException {
        final var URI = "/meals/" + meal1.getId();

        final var update = dumplingsWithIdDto();
        update.setRecipe(null);

        webTestClient.put().uri(URI)
                .body(BodyInserters.fromObject(update))
                .exchange()
                .expectStatus().isBadRequest();

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(meal1));
    }

    @Test
    @DisplayName("When update meal, and there is missing description, then throw BadRequestException")
    void updateMeal_whenUpdateHasNoDescription_throwBadRequestException() throws JsonProcessingException {
        final var URI = "/meals/" + meal1.getId();

        final var update = dumplingsWithIdDto();
        update.setDescription(null);

        webTestClient.put().uri(URI)
                .body(BodyInserters.fromObject(update))
                .exchange()
                .expectStatus().isBadRequest();

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(meal1));
    }

    @Test
    @DisplayName("When update meal, and there is missing imageUrl, then throw BadRequestException")
    void updateMeal_whenUpdateHasNoImageUrl_throwBadRequestException() throws JsonProcessingException {
        final var URI = "/meals/" + meal1.getId();

        final var update = dumplingsWithIdDto();
        update.setImageUrl(null);

        webTestClient.put().uri(URI)
                .body(BodyInserters.fromObject(update))
                .exchange()
                .expectStatus().isBadRequest();

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody().json(objectMapper.writeValueAsString(meal1));
    }

    @Test
    @DisplayName("When delete a meal, then the meal shouldn't be available in database")
    void deleteById() {
        final var URI = "/meals/" + meal1.getId();
        webTestClient.delete().uri(URI)
                .exchange()
                .expectStatus().isNoContent();

        webTestClient.get().uri(URI)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(NotFoundException.class);
    }

    private void createMeals() {
        meal1 = dumplingsWithId();
        meal2 = coffeeWithId();

        meal1 = mealService.save(meal1).block();
        meal2 = mealService.save(meal2).block();

        mealDto1 = mealDtoConverter.toDto(meal1);
        mealDto2 = mealDtoConverter.toDto(meal2);
    }

}