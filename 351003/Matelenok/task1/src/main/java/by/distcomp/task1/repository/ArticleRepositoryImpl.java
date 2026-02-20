package by.distcomp.task1.repository;

import by.distcomp.task1.model.Article;
import org.springframework.stereotype.Repository;
import java.time.OffsetDateTime;

@Repository
public class ArticleRepositoryImpl extends CrudRepositoryImpl<Article>
        implements ArticleRepository {

    @Override
    protected Long getId(Article article) {
        return article.getId();
    }
    @Override
    protected void setId(Article article, Long id) {
        article.setId(id);
    }
    protected void setCreated(Article article, OffsetDateTime dateTime ) { article.setCreated(dateTime); }
    protected void setModified(Article article, OffsetDateTime dateTime) {
        article.setModified(dateTime);
    }

    public Article save(Article article) {
        Long id = getId(article);
        OffsetDateTime now = OffsetDateTime.now();

        if (id == null) {
            id = idGenerator.incrementAndGet();
            setId(article, id);
            setCreated(article, now);
        } else {
            setModified(article, now);
        }
        storage.put(id, article);
        return article;
    }
}
