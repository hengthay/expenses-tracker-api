package com.hengthay.myapp.account;

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
