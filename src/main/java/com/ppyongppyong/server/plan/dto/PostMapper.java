package com.ppyongppyong.server.plan.dto;

import com.ppyongppyong.server.plan.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PostMapper {

    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

    PostResponseDto postToPostResponse(Post post, int orderIndex);
}
