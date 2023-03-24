package com.mysite.sbb.category;

import com.mysite.sbb.user.UserRole;
import com.mysite.sbb.user.UserSecurityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@Controller
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    private final UserSecurityService userSecurityService;

    @PreAuthorize("isAuthenticated() && hasRole('ROLE_ADMIN')")
    @GetMapping("/create")
    public String addCategory(CategoryForm categoryForm) {
        return "category_form";
    }

    @PreAuthorize("isAuthenticated() && hasRole('ROLE_ADMIN')")
    @PostMapping("/create")
    public String addCategory(@Valid CategoryForm categoryForm, BindingResult bindingResult) {
        categoryService.create(categoryForm.getName());
        return "redirect:/question/list";
    }
}
