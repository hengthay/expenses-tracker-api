package com.hengthay.myapp.category;

import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryDto> getAllCategories() {
        return categoryRepository
                .findAll()
                .stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    public CategoryDto getCategoryById(Long id) {
        var category = getCategory(id);

        return categoryMapper.toDto(category);
    }

    public CategoryDto createCategory(CategoryRequest request) {
        var category = categoryMapper.toEntity(request);
        categoryRepository.save(category);

        return categoryMapper.toDto(category);
    }

    public CategoryDto updateCategory(Long id, CategoryRequest request) {
        var category = getCategory(id);

        categoryMapper.update(request, category);

        categoryRepository.save(category);

        return categoryMapper.toDto(category);
    }

    public void deleteCategory(Long id) {
        var category = getCategory(id);

        categoryRepository.delete(category);
    }

    private Category getCategory(Long id) {
        var category = categoryRepository.findById(id).orElse(null);

        if(category == null)
            throw new CategoryNotFoundException();
        return category;
    }
}
