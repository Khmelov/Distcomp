package com.example.demo.sync;

import com.example.demo.cassandra.model.*;
import com.example.demo.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Profile("docker")
public class SyncService {

    private static final Logger log = LoggerFactory.getLogger(SyncService.class);
    private final RestTemplate restTemplate;
    private final String cassandraBaseUrl = "http://app-cassandra:24130";

    public SyncService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void syncAuthor(Author author, String action) {
        AuthorCassandra ca = new AuthorCassandra();
        ca.setId(author.getId());
        ca.setLogin(author.getLogin());
        ca.setPassword(author.getPassword());
        ca.setFirstname(author.getFirstname());
        ca.setLastname(author.getLastname());
        syncEntity("/api/v1.0/authors", ca, action);
    }

    public void syncNews(News news, String action) {
        NewsCassandra cn = new NewsCassandra();
        cn.setId(news.getId());
        cn.setAuthorId(news.getAuthor().getId());
        cn.setTitle(news.getTitle());
        cn.setContent(news.getContent());
        cn.setCreated(news.getCreated());
        cn.setModified(news.getModified());
        syncEntity("/api/v1.0/news", cn, action);
    }

    public void syncComment(Comment comment, String action) {
        CommentCassandra cc = new CommentCassandra();
        cc.setId(comment.getId());
        cc.setNewsId(comment.getNews().getId());
        cc.setContent(comment.getContent());
        syncEntity("/api/v1.0/comments", cc, action);
    }

    public void syncTag(Tag tag, String action) {
        TagCassandra ct = new TagCassandra();
        ct.setId(tag.getId());
        ct.setName(tag.getName());
        syncEntity("/api/v1.0/tags", ct, action);
    }

    private void syncEntity(String path, Object entity, String action) {
        String url = cassandraBaseUrl + path;
        try {
            switch (action.toLowerCase()) {
                case "create":
                    restTemplate.postForEntity(url, entity, Void.class);
                    log.info("Sync create success for {}", entity);
                    break;
                case "update":
                    restTemplate.exchange(url + "/" + getId(entity),
                            HttpMethod.PUT, new HttpEntity<>(entity), Void.class);
                    log.info("Sync update success for {}", entity);
                    break;
                case "delete":
                    restTemplate.delete(url + "/" + getId(entity));
                    log.info("Sync delete success for id {}", getId(entity));
                    break;
                default:
                    log.warn("Unknown sync action: {}", action);
            }
        } catch (Exception e) {
            log.error("Sync failed for {} {}: {}", action, url, e.getMessage());
        }
    }

    private Long getId(Object entity) {
        try {
            return (Long) entity.getClass().getMethod("getId").invoke(entity);
        } catch (Exception e) {
            throw new RuntimeException("Cannot get ID", e);
        }
    }
}