package by.bsuir.distcomp.service;

import by.bsuir.distcomp.PublisherApplication;
import by.bsuir.distcomp.dto.request.EditorRequestTo;
import by.bsuir.distcomp.repository.EditorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.CacheManager;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = PublisherApplication.class)
@ActiveProfiles("test")
@Testcontainers
@EmbeddedKafka(partitions = 1, topics = { "InTopic", "OutTopic" })
class EditorServiceCacheIntegrationTest {

    @Container
    @SuppressWarnings("resource")
    static final GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> String.valueOf(redis.getMappedPort(6379)));
    }

    @Autowired
    EditorService editorService;

    @SpyBean
    EditorRepository editorRepository;

    @Autowired
    CacheManager cacheManager;

    @Test
    void getByIdUsesRedisCacheSecondCall() {
        var req = new EditorRequestTo();
        req.setLogin("cacheuser");
        req.setPassword("password00");
        req.setFirstname("Test");
        req.setLastname("User");

        var created = editorService.create(req);
        assertThat(created.getId()).isNotNull();

        var editors = cacheManager.getCache("editors");
        assertThat(editors).isNotNull();
        editors.clear();
        clearInvocations(editorRepository);

        editorService.getById(created.getId());
        verify(editorRepository, atLeast(1)).findById(created.getId());
        clearInvocations(editorRepository);

        editorService.getById(created.getId());
        verify(editorRepository, never()).findById(created.getId());
    }
}
