package com.ppyongppyong.server.user.dto;

import com.ppyongppyong.server.user.entity.User;
import com.ppyongppyong.server.user.entity.UserRoleEnum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", imports = {UserRoleEnum.class})
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "password", source = "password")
    @Mapping(target = "userProfileImage", ignore = true)
    @Mapping(target = "userBackgroundImage", ignore = true)
    @Mapping(target = "introduce", ignore = true)
    @Mapping(target = "kakaoId", ignore = true)
    @Mapping(target = "naverId", ignore = true)
    @Mapping(target = "userRole", expression = "java(UserRoleEnum.USER)")
    User signupRequestDtoToEntity(String password, UserSignupRequestDto dto);

    UserLoginResponseDto entityToLoginResponseDto(User user, String accessToken, String refreshToken);
}
