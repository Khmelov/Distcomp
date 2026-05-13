package org.polozkov.security.comment;

import lombok.RequiredArgsConstructor;
import org.polozkov.repository.comment.CommentRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("commentSecurity") // Имя бина для @PreAuthorize
@RequiredArgsConstructor
public class CommentSecurity {

//    private final CommentRepository commentRepository;
//
//    public boolean isOwner(Long commentId) {
//        // 1. Получаем логин текущего пользователя из SecurityContext
//        String currentLogin = (String) SecurityContextHolder.getContext()
//                .getAuthentication().getPrincipal();
//
//        // 2. Ищем комментарий в базе и проверяем, что его автор — это текущий юзер
//        return commentRepository.findById(commentId)
//                .map(comment -> comment.getUser().getLogin().equals(currentLogin))
//                .orElse(false);
//    }
}