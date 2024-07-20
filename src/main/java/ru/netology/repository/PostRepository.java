package ru.netology.repository;

import org.springframework.beans.factory.annotation.Autowired;
import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;
import org.springframework.stereotype.Repository;


import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.List;

public class PostRepository {
  @Autowired
  private static final ConcurrentHashMap<Long, Post> posts = new ConcurrentHashMap<>();
  private static final AtomicLong counter = new AtomicLong(0);

  public List<Post> all() {
    return new ArrayList<>(posts.values());
  }

  public Optional<Post> getById(long id) {
    return Optional.ofNullable(posts.get(id));
  }

  public Post save(Post post) {
    if (post.getId() == 0) {
      long id = counter.incrementAndGet();
      post.setId(id);
      posts.put(id, post);
    } else {
      posts.computeIfPresent(post.getId(), (id, existingPost) -> {
        existingPost.setContent(post.getContent());
        return existingPost;
      });
      posts.computeIfAbsent(post.getId(), id -> {
        throw new NotFoundException("Post with id " + id + " not found");
      });
    }
    return post;
  }

  public void removeById(long id) {
    posts.remove(id);
  }
}
