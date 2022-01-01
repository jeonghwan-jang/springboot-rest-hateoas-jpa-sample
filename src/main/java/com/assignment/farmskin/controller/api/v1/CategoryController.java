package com.assignment.farmskin.controller.api.v1;

import com.assignment.farmskin.business.service.CategoryService;
import com.assignment.farmskin.business.vo.Category;
import com.assignment.farmskin.common.annotation.ApiV1Controller;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@ApiV1Controller
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    private final String PREFIX = "/categories";
    /**
     * GET /categories - 카테고리 리스트
     *
     * @return
     */
    @GetMapping(PREFIX)
    public ResponseEntity<?> list() {
        List<Category> categories = categoryService.list();

        return ResponseEntity.status(HttpStatus.OK).body(CollectionModel.of(categories.stream().map(EntityModel::of).map(category -> {
            category.add(
                Link.of(
                    linkTo(methodOn(BookController.class).list(null, null, null))
                        .toUriComponentsBuilder()
                        .queryParam("category", category.getContent().getId())
                        .build()
                        .toString()
                , "book-list-by-category")
            );
            return category;
        }).collect(Collectors.toList()), linkTo(methodOn(this.getClass()).list()).withSelfRel()));
    }
}
