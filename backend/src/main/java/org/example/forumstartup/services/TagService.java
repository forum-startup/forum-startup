package org.example.forumstartup.services;

import org.example.forumstartup.models.Tag;

import java.util.List;

public interface TagService {

    Tag findOrCreate(String name);

    Tag getByName(String name);

    List<Tag> getAll();

}
