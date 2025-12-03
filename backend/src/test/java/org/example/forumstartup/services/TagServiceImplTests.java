package org.example.forumstartup.services;

import org.example.forumstartup.exceptions.EntityNotFoundException;
import org.example.forumstartup.exceptions.InvalidTagFormatException;
import org.example.forumstartup.models.Tag;
import org.example.forumstartup.repositories.TagRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceImplTests {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagServiceImpl tagService;

    @Test
    void findOrCreate_ShouldReturnExistingTag_WhenTagAlreadyExists() {
        //Arrange
        Tag existing = new Tag();
        existing.setId(1L);
        existing.setName("name");

        when(tagRepository.findByName("name")).thenReturn(Optional.of(existing));

        //Act
        Tag result = tagService.findOrCreate("name");

        //Assert
        assertSame(existing, result);
        verify(tagRepository).findByName("name");
        verify(tagRepository, never()).save(any());
    }

    @Test
    void findOrCreate_ShouldCreateNewTag_WhenTagDoesNotExist() {
        //Arrange
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("name");

        when(tagRepository.save(any(Tag.class))).thenReturn(tag);

        //Act
        Tag saved = tagService.findOrCreate("name");

        //Assert
        assertEquals(1L, saved.getId());
        assertEquals("name", saved.getName());

        verify(tagRepository).findByName("name");
        verify(tagRepository).save(any(Tag.class));
    }


    @Test
    void findOrCreate_ShouldNormalizeName_BeforeChecking() {
        //Arrange
        Tag existing = new Tag();
        existing.setId(1L);
        existing.setName("tag");

        when(tagRepository.findByName("tag"))
                .thenReturn(Optional.of(existing));
        //Act
        Tag result = tagService.findOrCreate("tag");

        //Assert
        assertEquals("tag", result.getName());
        verify(tagRepository).findByName("tag");
    }

    @Test
    void findOrCreate_ShouldThrow_WhenTagIsNull() {
        //Act + Assert
        assertThrows(InvalidTagFormatException.class,
                () -> tagService.findOrCreate(null));
    }

    @Test
    void findOrCreate_ShouldThrow_WhenTagIsInvalid() {
        //Act + Assert
        assertThrows(InvalidTagFormatException.class,
                () -> tagService.findOrCreate("@XX"));
        verify(tagRepository, never()).findByName(any());
        verify(tagRepository, never()).save(any());
    }

    @Test
    void getByName_ShouldThrow_WhenNotFound() {

        when(tagRepository.findByName("name")).thenReturn(Optional.empty());

        //Act+Assert
        assertThrows(EntityNotFoundException.class,
                () -> tagService.getByName("name"));

        verify(tagRepository).findByName("name");
    }

    @Test
    void getAll_ShouldReturnListOfTags() {
        //Arrange
        Tag t1 = new Tag();
        t1.setId(1L);
        t1.setName("name");

        Tag t2 = new Tag();
        t2.setId(2L);
        t2.setName("spring");

        when(tagRepository.findAll()).thenReturn(List.of(t1, t2));

        //Act
        List<Tag> result = tagService.getAll();

        //Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(t1));
        assertTrue(result.contains(t2));
        verify(tagRepository).findAll();
    }
}