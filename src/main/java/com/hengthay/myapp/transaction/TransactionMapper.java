package com.hengthay.myapp.transaction;

import com.hengthay.myapp.account.AccountMapper;
import com.hengthay.myapp.category.CategoryMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

// uses to tell TransactionMapper to use AccountMapper and CategoryMapper
@Mapper(componentModel = "spring", uses = {AccountMapper.class, CategoryMapper.class})
public interface TransactionMapper {
    TransactionDto toDto(Transaction transaction);
    Transaction toEntity(TransactionCreateRequest request);
    void update(TransactionUpdateRequest request, @MappingTarget Transaction transaction);
}
