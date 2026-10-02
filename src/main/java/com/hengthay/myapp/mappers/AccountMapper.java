package com.hengthay.myapp.mappers;

import com.hengthay.myapp.dtos.AccountCreateRequest;
import com.hengthay.myapp.dtos.AccountDto;
import com.hengthay.myapp.dtos.RequestAccountUpdate;
import com.hengthay.myapp.entities.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    @Mapping(source = "user", target = "userDto")
    AccountDto toDto(Account account);
    Account toEntity(AccountCreateRequest request);
    void update(RequestAccountUpdate requestAccountUpdate, @MappingTarget Account account);
}
