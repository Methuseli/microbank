package com.microbank.client.utils;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import com.microbank.client.dto.UserUpdateRequest;
import com.microbank.client.entity.User;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    default void mapUserFromUserUpdateRequest(UserUpdateRequest userUpdateRequest, @MappingTarget User user) {
        if ( userUpdateRequest == null ) {
            return;
        }

        if ( userUpdateRequest.getBlacklisted() != null ) {
            user.setBlacklisted( userUpdateRequest.getBlacklisted() );
        }
        if ( userUpdateRequest.getEmail() != null ) {
            user.setEmail( userUpdateRequest.getEmail() );
        }
        if ( userUpdateRequest.getName() != null ) {
            user.setName( userUpdateRequest.getName() );
        }
        if ( userUpdateRequest.getRole() != null ) {
            user.setRole( userUpdateRequest.getRole() );
        }
    }
}