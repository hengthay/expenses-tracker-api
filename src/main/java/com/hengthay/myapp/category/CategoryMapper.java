package com.hengthay.myapp.category;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto toDto(Category category);
    Category toEntity(CategoryRequest request);
    void update(CategoryRequest request, @MappingTarget Category category);
}
