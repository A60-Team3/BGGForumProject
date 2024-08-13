package com.example.bggforumproject.repositories.contracts;

import com.example.bggforumproject.models.Comment;
import com.example.bggforumproject.helpers.filters.CommentFilterOptions;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CommentRepository extends OwnerRepository<Comment>{

    Page<Comment> get(CommentFilterOptions commentFilterOptions, int pageIndex, int pageSize);

    List<Comment> getCommentsForPost(long postId);

    Comment get(long id);

    void create(Comment comment);

    void update(Comment comment);

    void delete(long id);
}
