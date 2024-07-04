package ru.netology.controller;

import com.google.gson.Gson;
import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;
import ru.netology.service.PostService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;

public class PostController {
  public static final String APPLICATION_JSON = "application/json";
  private final PostService service;

  public PostController(PostService service) {
    this.service = service;
  }

  public void all(HttpServletResponse response) throws IOException {
    response.setContentType(APPLICATION_JSON);
    final var data = service.all();
    final var gson = new Gson();
    response.getWriter().print(gson.toJson(data));
  }

  public void getById(long id, HttpServletRequest req, HttpServletResponse response) throws IOException {
    String content = req.getParameter("content");
    if (id == 0) {
      if (content != null) {
        Post post = new Post(0, content);
        service.save(post);
        response.setContentType(APPLICATION_JSON);
        response.getWriter().print(new Gson().toJson(post));
      } else {
        throw new NotFoundException("Content parameter is missing");
      }
    } else {
      var existingPost = service.getById(id);
      if (existingPost != null) {
        System.out.println(existingPost);
        existingPost.setContent(content);
        service.save(existingPost);
        response.setContentType(APPLICATION_JSON);
        response.getWriter().print(new Gson().toJson(existingPost));
        System.out.println("Post with id " + id + " updated with new content: " + content);
      } else {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND); // Устанавливаем статус ответа 404
        response.getWriter().print("Пост с id " + id + " не найден.");
      }
    }
  }


  public void save(Reader body, HttpServletResponse response) throws IOException {
    response.setContentType(APPLICATION_JSON);
    final var gson = new Gson();
    final var post = gson.fromJson(body, Post.class);
    final var data = service.save(post);
    response.getWriter().print(gson.toJson(data));
  }

  public void removeById(long id, HttpServletResponse response) throws IOException {
    final var data = service.getById(id);
    final var gson = new Gson();
    response.getWriter().print(gson.toJson(data));
  }
}