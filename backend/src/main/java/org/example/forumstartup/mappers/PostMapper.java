package org.example.forumstartup.mappers;

import org.example.forumstartup.dtos.post.PostCreateDto;
import org.example.forumstartup.models.Post;
import org.example.forumstartup.dtos.post.PostResponseDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PostMapper {
    public PostResponseDto toDto(Post post) {
        List<String> tagNames = post.getTags()
                .stream()
                .map(t -> t.getName())
                .sorted()
                .toList();

        return new PostResponseDto(
                post.getId(),
                post.getCreator().getId(),
                post.getCreator().getUsername(),
                post.getTitle(),
                post.getContent(),
                post.getLikesCount(),
                tagNames,
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }

    public Post toPost(PostCreateDto dto) {
        return new Post(
                dto.title(),
                dto.content()
        );
    }

    public List<PostResponseDto> toDtoList(List<Post> posts) {
        List<PostResponseDto> list = new ArrayList<>();
        for (Post p : posts) {
            list.add(toDto(p));
        }
        return list;
    }
}
