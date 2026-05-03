package com.example.publisher.security;

import com.example.publisher.model.Article;
import com.example.publisher.model.Author;
import com.example.publisher.repository.ArticleRepository;
import com.example.publisher.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("securityEvaluator")
@RequiredArgsConstructor
public class SecurityEvaluator {

    private final AuthorRepository authorRepository;
    private final ArticleRepository articleRepository;

    public boolean isSelf(Long targetAuthorId, String currentLogin) {
        Author author = authorRepository.findByLogin(currentLogin).orElse(null);
        return author != null && author.getId().equals(targetAuthorId);
    }

    public boolean isArticleOwner(Long targetArticleId, String currentLogin) {
        Article article = articleRepository.findById(targetArticleId).orElse(null);
        return article != null && article.getAuthor().getLogin().equals(currentLogin);
    }

    // Примечание: Для Note, так как у вас нет authorId в Note, придется либо доверять статье,
    // к которой привязан Note, либо разрешить/запретить всем.
    // Для простоты реализации считаем, что владелец статьи владеет и нотами.
    public boolean isNoteOwner(Long noteId, String currentLogin) {
        // В рамках текущей архитектуры (через Kafka) получение Note синхронно может быть сложным.
        // Оставим базовую заглушку: разрешаем редактировать ноты только Админу, либо переделайте модель.
        return false;
    }
}