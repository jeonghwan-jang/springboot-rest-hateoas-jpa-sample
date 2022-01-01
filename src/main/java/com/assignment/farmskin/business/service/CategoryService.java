package com.assignment.farmskin.business.service;

import com.assignment.farmskin.business.repository.CategoryRepository;
import com.assignment.farmskin.business.vo.Category;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> list() {
        return categoryRepository.findAll();
    }

    public Category findById(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(EntityNotFoundException::new);
    }
}
