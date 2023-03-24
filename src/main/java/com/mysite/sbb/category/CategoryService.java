package com.mysite.sbb.category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category create(String name) {
        Category category = new Category();
        category.setName(name);
        category.setCreateDate(LocalDateTime.now());
        return categoryRepository.save(category);
    }
}
