package com.hengthay.myapp.mappers;

import com.hengthay.myapp.dtos.AccountDto;
import com.hengthay.myapp.entities.Account;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    AccountDto toDto(Account account);
    Account toEntity(AccountDto accountDto);
    void update(AccountDto accountDto, @MappingTarget Account account);
}
