package com.task310.blogplatform.repository;

import com.task310.blogplatform.model.Article;
import com.task310.blogplatform.model.Label;
import com.task310.blogplatform.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ArticleRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LabelRepository labelRepository;

    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser = new User();
        testUser.setLogin("testuser");
        testUser.setPassword("password123");
        testUser.setFirstname("Test");
        testUser.setLastname("User");
        testUser = userRepository.save(testUser);
    }

    @Test
    public void testSaveAndFindArticle() {
        Article article = new Article();
        article.setTitle("Test Article");
        article.setContent("Test Content");
        article.setUser(testUser);

        Article saved = articleRepository.save(article);
        assertThat(saved.getId()).isNotNull();

        Article found = articleRepository.findById(saved.getId()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getTitle()).isEqualTo("Test Article");
    }

    @Test
    public void testFindByUserId() {
        Article article = new Article();
        article.setTitle("Test Article");
        article.setContent("Test Content");
        article.setUser(testUser);
        articleRepository.save(article);

        List<Article> articles = articleRepository.findByUserId(testUser.getId());
        assertThat(articles).hasSize(1);
        assertThat(articles.get(0).getTitle()).isEqualTo("Test Article");
    }

    @Test
    public void testArticleWithLabels() {
        Label label1 = new Label();
        label1.setName("Technology");
        label1 = labelRepository.save(label1);

        Label label2 = new Label();
        label2.setName("Science");
        label2 = labelRepository.save(label2);

        Article article = new Article();
        article.setTitle("Test Article");
        article.setContent("Test Content");
        article.setUser(testUser);
        article.setLabels(Arrays.asList(label1, label2));
        article = articleRepository.save(article);

        Article found = articleRepository.findById(article.getId()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getLabels()).hasSize(2);
    }
}

