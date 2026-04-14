package by.bsuir.distcomp.discussion.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReactionIdGeneratorTest {

    @Test
    void nextId_increments() {
        ReactionIdGenerator g = new ReactionIdGenerator();
        long a = g.nextId();
        long b = g.nextId();
        assertThat(b).isGreaterThan(a);
    }
}
