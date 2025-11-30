package org.example.forumstartup.mappers;

import lombok.RequiredArgsConstructor;
import org.example.forumstartup.dtos.post.PostCreateDto;
import org.example.forumstartup.dtos.post.PostUpdateDto;
import org.example.forumstartup.dtos.post.PostWithLikeStatusResponseDto;
import org.example.forumstartup.models.Post;
import org.example.forumstartup.dtos.post.PostResponseDto;
import org.example.forumstartup.models.Tag;
import org.example.forumstartup.models.User;
import org.example.forumstartup.utils.AuthenticationUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component
@RequiredArgsConstructor
public class PostMapper {

    private final AuthenticationUtils authenticationUtils;

    public PostResponseDto toSimpleDto(Post post) {
        List<String> tagNames = post.getTags()
                .stream()
                .map(Tag::getName)
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

    public Post toPostWhenUpdate(PostUpdateDto dto) {
        return new Post(
                dto.title(),
                dto.content()
        );
    }

    public List<PostResponseDto> toSimpleDtoList(List<Post> posts) {
        List<PostResponseDto> list = new ArrayList<>();
        for (Post p : posts) {
            list.add(toSimpleDto(p));
        }
        return list;
    }

    // Post Response when user logged in (includes information if a user has liked the post or not)
    public List<PostWithLikeStatusResponseDto> toAuthenticatedDtoList(List<Post> posts, User currentUser) {
        return posts.stream()
                .map(post -> toAuthenticatedDto(post, currentUser))
                .toList();
    }

    public PostWithLikeStatusResponseDto toAuthenticatedDto(Post post, User currentUser) {
        List<String> tags = post.getTags().stream()
                .map(Tag::getName)
                .sorted()
                .toList();

        boolean liked = post.getLikedBy().contains(currentUser);

        return new PostWithLikeStatusResponseDto(
                post.getId(),
                post.getCreator().getId(),
                post.getCreator().getUsername(),
                post.getTitle(),
                post.getContent(),
                post.getLikesCount(),
                liked,
                tags,
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}
