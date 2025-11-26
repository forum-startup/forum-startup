package org.example.forumstartup.services;

import lombok.RequiredArgsConstructor;
import org.example.forumstartup.exceptions.EntityNotFoundException;
import org.example.forumstartup.exceptions.InvalidTagFormatException;
import org.example.forumstartup.models.Tag;
import org.example.forumstartup.repositories.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        if (cleaned.length() < TAG_MIN_LENGTH || cleaned.length() > TAG_MAX_LENGTH)
            throw new InvalidTagFormatException(
                    String.format(INVALID_TAG_LENGTH, TAG_MIN_LENGTH, TAG_MAX_LENGTH)
            );

        if (!cleaned.matches(TAG_NORMALIZED_PATTERN))
            throw new InvalidTagFormatException(TAG_NORMALIZED_PATTERN_MESSAGE);

        return cleaned;
    }
}
