package by.bsuir.rest.repository;

import by.bsuir.rest.entity.Marker;
import by.bsuir.rest.entity.Message;
import by.bsuir.rest.entity.Story;
import by.bsuir.rest.entity.User;
import by.bsuir.rest.service.StorySearchCriteria;
import by.bsuir.rest.support.PostgresTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class EntityRepositoryTest extends PostgresTestSupport {

    @Autowired
    private UserRepository users;

    @Autowired
    private StoryRepository stories;

    @Autowired
    private MarkerRepository markers;

    @Autowired
    private MessageRepository messages;

    @Autowired
    private JdbcTemplate jdbc;

    @BeforeEach
    void cleanTables() {
        jdbc.update("DELETE FROM distcomp.tbl_story_marker");
        jdbc.update("DELETE FROM distcomp.tbl_message");
        jdbc.update("DELETE FROM distcomp.tbl_story");
        jdbc.update("DELETE FROM distcomp.tbl_marker");
        jdbc.update("DELETE FROM distcomp.tbl_user");
    }

    @Test
    void createsSchemaTables() {
        List<String> tables = jdbc.queryForList(
                "select table_name from information_schema.tables where table_schema = 'distcomp'",
                String.class);
        assertThat(tables).contains("tbl_user", "tbl_story", "tbl_marker", "tbl_message", "tbl_story_marker");
    }

    @Test
    void storesUserWithPaginationFilterAndSort() {
        users.save(user("page-b", "Борис", "Второй"));
        users.save(user("page-a", "Анна", "Первая"));
        User third = users.save(user("page-c", "Анна", "Третья"));

        PageResult<User> byLogin = users.find(new UserFilter("page-c", "Анна", "Третья"), PageQuery.all());
        assertThat(byLogin.content()).extracting(User::getId).containsExactly(third.getId());

        PageResult<User> firstPage = users.find(UserFilter.empty(), PageQuery.page(0, 1, "login", false));
        assertThat(firstPage.totalElements()).isEqualTo(3);
        assertThat(firstPage.content()).extracting(User::getLogin).containsExactly("page-c");

        PageResult<User> secondPage = users.find(UserFilter.empty(), PageQuery.page(1, 1, "login", false));
        assertThat(secondPage.content()).extracting(User::getLogin).containsExactly("page-b");

        third.setLastname("Изменённая");
        users.save(third);
        assertThat(users.findById(third.getId()).orElseThrow().getLastname()).isEqualTo("Изменённая");
        assertThat(users.deleteById(third.getId())).isTrue();
        assertThat(users.deleteById(third.getId())).isFalse();
        assertThat(users.existsById(third.getId())).isFalse();
    }

    @Test
    void storesMarkerWithNameFilterAndSort() {
        markers.save(marker("m-a"));
        markers.save(marker("m-c"));
        Marker middle = markers.save(marker("m-b"));

        assertThat(markers.find(new MarkerFilter("m-b"), PageQuery.all()).content())
                .extracting(Marker::getId)
                .containsExactly(middle.getId());

        PageResult<Marker> page = markers.find(MarkerFilter.empty(), PageQuery.page(0, 2, "name", true));
        assertThat(page.totalElements()).isEqualTo(3);
        assertThat(page.content()).extracting(Marker::getName).containsExactly("m-a", "m-b");
        assertThat(page.page()).isZero();
        assertThat(page.size()).isEqualTo(2);
    }

    @Test
    void storesMessageFilteredByStoryAndContent() {
        Message first = messages.save(message(11L, "alpha-text"));
        messages.save(message(11L, "beta-text"));
        messages.save(message(12L, "alpha-text"));

        PageResult<Message> found = messages.find(new MessageFilter(11L, "alpha-text"), PageQuery.all());
        assertThat(found.content()).extracting(Message::getId).containsExactly(first.getId());

        PageResult<Message> sorted = messages.find(MessageFilter.empty(), PageQuery.page(0, 2, "content", false));
        assertThat(sorted.totalElements()).isEqualTo(3);
        assertThat(sorted.content()).extracting(Message::getContent).containsExactly("beta-text", "alpha-text");
    }

    @Test
    void filtersStoriesByAuthorMarkersAndKeepsRowsAfterParentDelete() {
        User author = users.save(user("author-login", "Иван", "Автор"));
        User other = users.save(user("other-login", "Пётр", "Другой"));
        Marker alpha = markers.save(marker("alpha"));
        Marker beta = markers.save(marker("beta"));

        Story matched = story(author.getId(), "matched-title", "matched-content");
        matched.setMarkerIds(Set.of(alpha.getId(), beta.getId()));
        matched = stories.save(matched);

        Story partial = story(author.getId(), "partial-title", "partial-content");
        partial.setMarkerIds(Set.of(alpha.getId()));
        stories.save(partial);

        Story foreign = story(other.getId(), "foreign-title", "matched-content");
        stories.save(foreign);

        PageResult<Story> found = stories.find(new StorySearchCriteria(
                List.of(alpha.getName(), beta.getName()),
                List.of(alpha.getId(), beta.getId()),
                author.getLogin(),
                "matched-title",
                "matched-content"
        ), PageQuery.all());
        assertThat(found.content()).extracting(Story::getId).containsExactly(matched.getId());
        assertThat(found.content().get(0).getMarkerIds()).containsExactlyInAnyOrder(alpha.getId(), beta.getId());

        Message message = messages.save(message(matched.getId(), "kept-message"));
        assertThat(users.deleteById(author.getId())).isTrue();
        Story afterUserDelete = stories.findById(matched.getId()).orElseThrow();
        assertThat(afterUserDelete.getUserId()).isEqualTo(author.getId());

        assertThat(markers.deleteById(alpha.getId())).isTrue();
        assertThat(stories.findById(matched.getId()).orElseThrow().getMarkerIds())
                .containsExactly(beta.getId());

        assertThat(stories.deleteById(matched.getId())).isTrue();
        assertThat(messages.findById(message.getId()).orElseThrow().getStoryId()).isEqualTo(matched.getId());
        assertThat(messages.findById(message.getId()).orElseThrow().getContent()).isEqualTo("kept-message");
    }

    private User user(String login, String firstname, String lastname) {
        User user = new User();
        user.setLogin(login);
        user.setPassword("password123");
        user.setFirstname(firstname);
        user.setLastname(lastname);
        return user;
    }

    private Marker marker(String name) {
        Marker marker = new Marker();
        marker.setName(name);
        return marker;
    }

    private Message message(Long storyId, String content) {
        Message message = new Message();
        message.setStoryId(storyId);
        message.setContent(content);
        return message;
    }

    private Story story(Long userId, String title, String content) {
        Story story = new Story();
        story.setUserId(userId);
        story.setTitle(title);
        story.setContent(content);
        Instant now = Instant.parse("2026-11-12T09:00:00Z");
        story.setCreated(now);
        story.setModified(now);
        return story;
    }
}
