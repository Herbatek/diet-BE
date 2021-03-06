package com.piotrek.diet.product;

import com.piotrek.diet.helpers.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

import static com.piotrek.diet.helpers.Page.DEFAULT_PAGE_SIZE;
import static com.piotrek.diet.helpers.Page.FIRST_PAGE_NUM;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    Mono<Page<ProductDto>> findAll(
            @RequestParam(defaultValue = FIRST_PAGE_NUM) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size) {
        return productService.findAllPageable(PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    Mono<ProductDto> findById(@PathVariable String id) {
        return productService.findDtoById(id);
    }

    @GetMapping("/search")
    Mono<Page<ProductDto>> searchByName(
            @RequestParam(defaultValue = FIRST_PAGE_NUM) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = "") String query) {
        return productService.searchByName(PageRequest.of(page, size), query);
    }

    @PutMapping("/{id}")
    Mono<ProductDto> updateProduct(@PathVariable String id, @Valid ProductDto productUpdate) {
        return productService.updateProduct(id, productUpdate);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    Mono<Void> deleteById(@PathVariable String id) {
        return productService.deleteById(id);
    }
}
