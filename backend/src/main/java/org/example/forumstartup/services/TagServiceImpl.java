package org.example.forumstartup.services;

import lombok.RequiredArgsConstructor;
import org.example.forumstartup.exceptions.EntityNotFoundException;
import org.example.forumstartup.exceptions.InvalidTagFormatException;
import org.example.forumstartup.models.Tag;
import org.example.forumstartup.repositories.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.example.forumstartup.utils.TagConstants.*;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    @Transactional
    public Tag findOrCreate(String name) {
        String normalized = normalize(name);

        return tagRepository.findByName(normalized)
                .orElseGet(() -> {
                    Tag newTag = new Tag();
                    newTag.setName(normalized);
                    return tagRepository.save(newTag);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Tag getByName(String name) {
        String normalized = normalize(name);

        return tagRepository.findByName(normalized)
                .orElseThrow(() ->
                        new EntityNotFoundException("Tag", "name", normalized)
                );
    }

    private String normalize(String raw) {
        if (raw == null)
            throw new InvalidTagFormatException("Tag cannot be null.");

        // Trim + lowercase + replace 1+ spaces with hyphens
        String cleaned = raw.trim().toLowerCase()
                .replaceAll("\\s+", "-")
                .replaceAll("-{2,}", "-");

        if (!cleaned.matches(TAG_NORMALIZED_PATTERN))
            throw new InvalidTagFormatException(TAG_NORMALIZED_PATTERN_MESSAGE);

        return cleaned;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tag> getAll() {
        return tagRepository.findAll();
    }

}
