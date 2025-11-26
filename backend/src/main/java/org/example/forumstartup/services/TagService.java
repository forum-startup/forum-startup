package org.example.forumstartup.services;

import org.example.forumstartup.models.Tag;

public interface TagService {

    Tag findOrCreate(String name);

    Tag getByName(String name);

}
