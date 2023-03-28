package com.mysite.sbb.category;

import com.mysite.sbb.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    public List<Category> getList() {
        return categoryRepository.findAll();
    }

    public Optional<Category> get(String categoryName) {
        if (categoryName.isEmpty() || categoryName.isBlank()) {
            return Optional.empty();
        }

        Optional<Category> categoryOptional = categoryRepository.findByName(categoryName);
        if (categoryOptional.isPresent()) {
            return categoryOptional;
        }
        throw new DataNotFoundException("카테고리가 존재하지 않습니다.");
    }

    public Optional<Category> get(Optional<String> categoryName) {
        if (categoryName.isPresent()) {
            return get(categoryName.get());
        }
        return Optional.empty();
    }

    public Category get(Integer id) {
        Optional<Category> categoryOptional = categoryRepository.findById(id);
        if (categoryOptional.isPresent()) {
            return categoryOptional.get();
        }
        throw new DataNotFoundException("카테고리가 존재하지 않습니다.");
    }

    public void delete(Category category) {
        categoryRepository.delete(category);
    }
}
