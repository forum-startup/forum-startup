package org.example.forumstartup.helpers;

import org.example.forumstartup.models.Post;
import org.example.forumstartup.models.PostResponseDto;

import java.util.ArrayList;
import java.util.List;

public class PostMapper {
    public static PostResponseDto toDto(Post post) {
        PostResponseDto dto = new PostResponseDto();
        dto.setPostId(post.getId());
        dto.setCreatorId(post.getCreator().getId());
        dto.setCreatorUsername(post.getCreator().getUsername());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setLikesCount(post.getLikesCount());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());
        return dto;
    }

    public static List<PostResponseDto> toDtoList(List<Post> posts) {
        List<PostResponseDto> list = new ArrayList<>();
        for (Post p : posts) {
            list.add(toDto(p));
        }
        return list;
    }
}
