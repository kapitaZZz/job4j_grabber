package ru.job4j.grabber;

import ru.job4j.grabber.dto.Post;

import java.io.IOException;
import java.util.List;

public interface Parse {
    List<Post> list(String link) throws IOException;
}
