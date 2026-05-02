package com.example.demo.validator;

import com.example.demo.entity.Author;
import com.example.demo.entity.Comment;
import com.example.demo.entity.News;
import com.example.demo.repository.AuthorRepository;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.NewsRepository;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;

@Profile("!cassandra")
@Component("ownershipValidator")
public class OwnershipValidator {

    private final AuthorRepository authorRepository;
    private final NewsRepository newsRepository;
    private final CommentRepository commentRepository;

    public OwnershipValidator(AuthorRepository authorRepository,
                              NewsRepository newsRepository,
                              CommentRepository commentRepository) {
        this.authorRepository = authorRepository;
        this.newsRepository = newsRepository;
        this.commentRepository = commentRepository;
    }

    public boolean isAuthorOwner(Long authorId, String login) {
        Author author = authorRepository.findByLogin(login).orElse(null);
        return author != null && author.getId().equals(authorId);
    }

    public boolean isNewsOwner(Long newsId, String login) {
        Author author = authorRepository.findByLogin(login).orElse(null);
        if (author == null) return false;
        News news = newsRepository.findById(newsId).orElse(null);
        return news != null && news.getAuthor().getId().equals(author.getId());
    }

    public boolean isCommentOwner(Long commentId, String login) {
        Author author = authorRepository.findByLogin(login).orElse(null);
        if (author == null) return false;
        Comment comment = commentRepository.findById(commentId).orElse(null);

        return comment != null && comment.getNews().getAuthor().getId().equals(author.getId());
    }
}