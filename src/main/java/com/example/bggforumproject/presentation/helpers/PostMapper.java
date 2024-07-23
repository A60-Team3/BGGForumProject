package com.example.bggforumproject.presentation.helpers;

import com.example.bggforumproject.persistance.models.Post;
import com.example.bggforumproject.presentation.dtos.PostDTO;
import com.example.bggforumproject.service.PostService;
import org.springframework.stereotype.Component;


@Component
public class PostMapper {

    private final PostService postService;

    public PostMapper(PostService postService) {
        this.postService = postService;
    }

    public Post fromDto(PostDTO dto){
        Post post = new Post();
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        return post;
    }

    public Post fromDto(int id, PostDTO dto){
        Post post = fromDto(dto);
        post.setId(id);
        Post repositoryPost = postService.get(id);
        post.setUserId(repositoryPost.getUserId());
        post.setCreatedAt(repositoryPost.getCreatedAt());
        return post;
    }
}
