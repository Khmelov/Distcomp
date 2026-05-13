package by.bsuir.discussion.service;

import by.bsuir.discussion.exception.DiscussionNotFoundException;
import by.bsuir.distcomp.dto.CommentDto;
import by.bsuir.distcomp.dto.CommentState;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.Row;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DiscussionCommentService {
    private final CqlSession session;
    private final String tableName;
    private final AtomicLong idSequence = new AtomicLong();

    public DiscussionCommentService(CqlSession session, @Value("${discussion.cassandra.keyspace}") String keyspace) {
        this.session = session;
        this.tableName = keyspace + ".tbl_comment";
    }

    @PostConstruct
    void initSequence() {
        long maxId = session.execute("SELECT id FROM " + tableName)
                .all()
                .stream()
                .mapToLong(row -> row.getLong("id"))
                .max()
                .orElse(0L);
        idSequence.set(maxId);
    }

    public CommentDto create(CommentDto dto) {
        long id = dto.id() == null ? idSequence.incrementAndGet() : dto.id();
        CommentState state = moderate(dto.content());
        session.execute("INSERT INTO " + tableName + " (id, issue_id, content, state) VALUES (?, ?, ?, ?)",
                id, dto.issueId(), dto.content(), state.name());
        idSequence.accumulateAndGet(id, Math::max);
        return new CommentDto(id, dto.issueId(), dto.content(), state);
    }

    public CommentDto get(Long id) {
        Row row = session.execute("SELECT id, issue_id, content, state FROM " + tableName + " WHERE id = ?", id).one();
        if (row == null) {
            throw new DiscussionNotFoundException("Comment not found");
        }
        return toDto(row);
    }

    public List<CommentDto> findAll(Pageable pageable) {
        int offset = Math.max(0, (int) pageable.getOffset());
        int limit = pageable.getPageSize();
        return session.execute("SELECT id, issue_id, content, state FROM " + tableName)
                .all()
                .stream()
                .map(this::toDto)
                .sorted((left, right) -> Long.compare(left.id(), right.id()))
                .skip(offset)
                .limit(limit)
                .toList();
    }

    public CommentDto update(Long id, CommentDto dto) {
        get(id);
        CommentState state = moderate(dto.content());
        session.execute("UPDATE " + tableName + " SET issue_id = ?, content = ?, state = ? WHERE id = ?",
                dto.issueId(), dto.content(), state.name(), id);
        return new CommentDto(id, dto.issueId(), dto.content(), state);
    }

    public void delete(Long id) {
        get(id);
        session.execute("DELETE FROM " + tableName + " WHERE id = ?", id);
    }

    private CommentDto toDto(Row row) {
        String state = row.getString("state");
        return new CommentDto(
                row.getLong("id"),
                row.getLong("issue_id"),
                row.getString("content"),
                state == null ? CommentState.APPROVE : CommentState.valueOf(state));
    }

    private CommentState moderate(String content) {
        String normalized = content == null ? "" : content.toLowerCase();
        if (normalized.contains("spam") || normalized.contains("bad") || normalized.contains("decline")) {
            return CommentState.DECLINE;
        }
        return CommentState.APPROVE;
    }
}
