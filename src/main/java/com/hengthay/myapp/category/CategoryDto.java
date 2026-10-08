package com.hengthay.myapp.category;

import lombok.Data;

import java.time.Instant;

@Data
public class CategoryDto {
    private Long id;
    private String name;
    private Instant createdAt;
}
