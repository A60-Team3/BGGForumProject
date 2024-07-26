package com.example.bggforumproject.helpers;

import com.example.bggforumproject.models.Post;
import com.example.bggforumproject.dtos.PostCreateDTO;
import com.example.bggforumproject.service.contacts.PostService;
import org.springframework.stereotype.Component;


@Component
public class PostMapper {

    private final PostService postService;

    public PostMapper(PostService postService) {
        this.postService = postService;
    }

    public Post fromDto(PostCreateDTO dto){
        Post post = new Post();
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        return post;
    }

    public Post fromDto(int id, PostCreateDTO dto){
        Post post = fromDto(dto);
        post.setId(id);
        Post repositoryPost = postService.get(id);
        post.setUserId(repositoryPost.getUserId());
        post.setCreatedAt(repositoryPost.getCreatedAt());
        return post;
    }
}
