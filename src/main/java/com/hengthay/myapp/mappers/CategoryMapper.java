package com.hengthay.myapp.mappers;

import com.hengthay.myapp.dtos.CategoryDto;
import com.hengthay.myapp.dtos.CategoryRequest;
import com.hengthay.myapp.entities.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto toDto(Category category);
    Category toEntity(CategoryRequest request);
    void update(CategoryRequest request, @MappingTarget Category category);
}
