package com.mysite.sbb.category;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PreAuthorize("isAuthenticated() && hasRole('ROLE_ADMIN')")
    @GetMapping("/create")
    public String addCategory(CategoryForm categoryForm) {
        return "category_form";
    }

    @PreAuthorize("isAuthenticated() && hasRole('ROLE_ADMIN')")
    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Integer id) {
        Category category = categoryService.get(id);
        categoryService.delete(category);
        return "redirect:/question/list";
    }

    @PreAuthorize("isAuthenticated() && hasRole('ROLE_ADMIN')")
    @PostMapping("/create")
    public String addCategory(@Valid CategoryForm categoryForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "category_form";
        }
        categoryService.create(categoryForm.getName());
        return "redirect:/question/list";
    }
}
