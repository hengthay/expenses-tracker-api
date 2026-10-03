package com.hengthay.myapp.mappers;

import com.hengthay.myapp.dtos.TransactionCreateRequest;
import com.hengthay.myapp.dtos.TransactionDto;
import com.hengthay.myapp.dtos.TransactionUpdateRequest;
import com.hengthay.myapp.entities.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    TransactionDto toDto(Transaction transaction);
    Transaction toEntity(TransactionCreateRequest request);
    void update(TransactionUpdateRequest request, @MappingTarget Transaction transaction);
}
