package com.distcomp.discussion.service;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.Row;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Allocates identifiers for new notices.
 *
 * <p>Cassandra has no sequences. A one-row counter table provides a cluster-wide
 * monotonic value; the in-memory guard keeps identifiers strictly increasing on
 * the local node even if a counter read is served by a slightly stale replica.</p>
 */
@Component
public class NoticeIdGenerator {

    private final CqlSession session;

    private final AtomicLong lastIssued = new AtomicLong();

    public NoticeIdGenerator(CqlSession session) {
        this.session = session;
    }

    public long nextId() {
        session.execute("UPDATE distcomp.tbl_notice_counter SET value = value + 1 WHERE name = 'notice'");
        Row row = session.execute(
                "SELECT value FROM distcomp.tbl_notice_counter WHERE name = 'notice'").one();
        long fromDatabase = row == null || row.isNull("value") ? 0L : row.getLong("value");
        return lastIssued.updateAndGet(previous -> Math.max(previous + 1, fromDatabase));
    }
}
