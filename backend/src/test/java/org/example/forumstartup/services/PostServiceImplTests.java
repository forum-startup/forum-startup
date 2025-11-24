package org.example.forumstartup.services;


import org.example.forumstartup.exceptions.EntityNotFoundException;
import org.example.forumstartup.models.Post;
import org.example.forumstartup.repositories.PostRepository;
import org.example.forumstartup.repositories.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceImplTests {
    @Mock
    private PostRepository postRepository;
    @Mock
    private TagRepository tagRepository;
    @InjectMocks
    private PostServiceImpl postService;

    Post basePost;

    @BeforeEach
    void init() {
        basePost = new Post();
        basePost.setId(1L);
    }

    private Post createPost(Long id) {
        Post p = new Post();
        p.setId(id);
        return p;
    }

    @Test
    void getById_returnPost_whenPostExists() {

        //Arrange
        init();
        when(postRepository.findById(1L)).thenReturn(Optional.of(basePost));
        //Act
        Post result = postService.getById(1L);

        //Assert
        assertEquals(1L, result.getId());
        verify(postRepository).findById(1L);
    }

    @Test
    void getById_throwsException_whenPostDoesNotExist() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(EntityNotFoundException.class,
                () -> postService.getById(1L));

        verify(postRepository).findById(1L);
    }
}