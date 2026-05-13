package org.polozkov.security.issue;

import lombok.RequiredArgsConstructor;
import org.polozkov.entity.issue.Issue;
import org.polozkov.repository.issue.IssueRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("issueSecurity")
@RequiredArgsConstructor
public class IssueSecurity {

    private final IssueRepository issueRepository;

    /**
     * Проверяет, является ли текущий авторизованный пользователь
     * создателем (владельцем) данной задачи.
     *
     * @param issueId ID задачи из параметров запроса
     * @return true, если логин из токена совпадает с логином автора задачи
     */
    public boolean isOwner(Long issueId) {
        if (issueId == null) {
            return false;
        }

        // 1. Извлекаем login текущего пользователя из SecurityContext
        // (Тот самый principal, который мы устанавливали в JwtTokenFilter)
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof String)) {
            return false;
        }

        String currentLogin = (String) principal;

        // 2. Ищем задачу в репозитории и сравниваем логин владельца
        // Используем findById, чтобы избежать лишних исключений до проверки
        return issueRepository.findById(issueId)
                .map(issue -> issue.getUser().getLogin().equals(currentLogin))
                .orElse(false);
    }
}