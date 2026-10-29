package com.hengthay.myapp.user;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    // mapping to dtos
    UserDto toDto(User user);
    // mapping to entity
    User toEntity(RegisterUserDto userDto);
    // update mapping (modifies the existing User entity in place)
    void update(RequestUserUpdate request, @MappingTarget User user);
    // update by admin
    void updateByAdmin(RequestAdminUserByAdmin request, @MappingTarget User user);
    // admin dtos
    AdminUpdateUserDto toAdminDto(User user);
}
