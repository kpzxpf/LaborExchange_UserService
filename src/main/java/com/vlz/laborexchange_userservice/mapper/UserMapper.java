package com.vlz.laborexchange_userservice.mapper;

import com.vlz.laborexchange_userservice.dto.UserDto;
import com.vlz.laborexchange_userservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "role.name", target = "roleName")
    UserDto toDto(User user);
}
