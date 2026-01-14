package com.distcomp.publisher.security;

import com.distcomp.publisher.article.repo.ArticleRepository;
import com.distcomp.publisher.post.client.KafkaPostClient;
import com.distcomp.publisher.post.dto.PostResponse;
import com.distcomp.publisher.writer.domain.Writer;
import com.distcomp.publisher.writer.repo.WriterRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service("access")
public class AccessService {

    private final WriterRepository writerRepository;
    private final ArticleRepository articleRepository;
    private final KafkaPostClient kafkaPostClient;

    public AccessService(WriterRepository writerRepository, ArticleRepository articleRepository, KafkaPostClient kafkaPostClient) {
        this.writerRepository = writerRepository;
        this.articleRepository = articleRepository;
        this.kafkaPostClient = kafkaPostClient;
    }

    public boolean isAdmin() {
        return SecurityUtils.isAdmin();
    }

    public boolean isSelfWriterId(long writerId) {
        String login = SecurityUtils.getCurrentLogin();
        if (login == null) {
            return false;
        }
        Optional<Writer> writerOpt = writerRepository.findById(writerId);
        return writerOpt.map(w -> login.equals(w.getLogin())).orElse(false);
    }

    public boolean isSelfWriterLogin(String writerLogin) {
        String login = SecurityUtils.getCurrentLogin();
        return login != null && login.equals(writerLogin);
    }

    public boolean isSelfArticleId(Long articleId) {
        if (articleId == null) {
            return false;
        }
        String login = SecurityUtils.getCurrentLogin();
        if (login == null) {
            return false;
        }
        return articleRepository.findById(articleId)
                .map(a -> a.getWriter() != null && login.equals(a.getWriter().getLogin()))
                .orElse(false);
    }

    public boolean isSelfPostId(Long postId) {
        if (postId == null) {
            return false;
        }
        PostResponse post = kafkaPostClient.getById(postId);
        if (post == null) {
            return false;
        }
        return isSelfArticleId(post.getArticleId());
    }

    public boolean canWriteWriter(long writerId) {
        return isAdmin() || isSelfWriterId(writerId);
    }

    public boolean canWriteArticle(long writerIdFromRequest, Long articleId) {
        if (isAdmin()) {
            return true;
        }

        if (articleId != null) {
            return isSelfArticleId(articleId);
        }

        return isSelfWriterId(writerIdFromRequest);
    }
}
