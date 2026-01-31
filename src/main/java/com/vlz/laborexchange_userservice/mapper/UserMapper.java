package com.vlz.laborexchange_userservice.mapper;

import com.vlz.laborexchange_userservice.dto.UserDto;
import com.vlz.laborexchange_userservice.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
}
