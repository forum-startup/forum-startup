package org.example.forumstartup.mappers;

import org.example.forumstartup.models.Post;
import org.example.forumstartup.dtos.post.PostResponseDto;

import java.util.ArrayList;
import java.util.List;

public class PostMapper {
    public static PostResponseDto toDto(Post post) {
        return new PostResponseDto(
                post.getId(),
                post.getCreator().getId(),
                post.getCreator().getUsername(),
                post.getTitle(),
                post.getContent(),
                post.getLikesCount(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }

    public static List<PostResponseDto> toDtoList(List<Post> posts) {
        List<PostResponseDto> list = new ArrayList<>();
        for (Post p : posts) {
            list.add(toDto(p));
        }
        return list;
    }
}
