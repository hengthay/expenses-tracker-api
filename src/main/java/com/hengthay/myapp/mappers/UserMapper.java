package com.hengthay.myapp.mappers;

import com.hengthay.myapp.dtos.RegisterUserDto;
import com.hengthay.myapp.dtos.RequestUserUpdate;
import com.hengthay.myapp.dtos.UserDto;
import com.hengthay.myapp.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
    User toEntity(RegisterUserDto userDto);
    void update(RequestUserUpdate request, @MappingTarget User user);
}
