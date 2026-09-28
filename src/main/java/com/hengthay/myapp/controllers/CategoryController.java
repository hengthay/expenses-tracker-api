package com.hengthay.myapp.controllers;

import com.hengthay.myapp.dtos.CategoryDto;
import com.hengthay.myapp.dtos.CategoryRequest;
import com.hengthay.myapp.mappers.CategoryMapper;
import com.hengthay.myapp.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@AllArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @GetMapping
    public List<CategoryDto> getAllCategories() {
        return categoryRepository
                .findAll()
                .stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @RequestMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Long id) {
        var category = categoryRepository.findById(id).orElse(null);

        if(category == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(categoryMapper.toDto(category));
    }

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(
            @RequestBody CategoryRequest request
            ) {
        var category = categoryMapper.toEntity(request);
        categoryRepository.save(category);
        var categoryDto = categoryMapper.toDto(category);

        return ResponseEntity.status(HttpStatus.CREATED).body(categoryDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryRequest request
    ) {
        var category = categoryRepository.findById(id).orElse(null);

        if(category == null)
            return ResponseEntity.notFound().build();

        categoryMapper.update(request, category);

        categoryRepository.save(category);

        return ResponseEntity.ok(categoryMapper.toDto(category));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        var category = categoryRepository.findById(id).orElse(null);

        if(category == null)
            return ResponseEntity.notFound().build();

        categoryRepository.delete(category);

        return ResponseEntity.noContent().build();
    }
}
