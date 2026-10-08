package com.hengthay.myapp.transaction;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    TransactionDto toDto(Transaction transaction);
    Transaction toEntity(TransactionCreateRequest request);
    void update(TransactionUpdateRequest request, @MappingTarget Transaction transaction);
}
