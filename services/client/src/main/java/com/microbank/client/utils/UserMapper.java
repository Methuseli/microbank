package com.microbank.client.utils;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.microbank.client.dto.UserUpdateRequest;
import com.microbank.client.entity.User;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    void mapUserToUserUpdateRequest(UserUpdateRequest userUpdateRequest, @MappingTarget User user);
}