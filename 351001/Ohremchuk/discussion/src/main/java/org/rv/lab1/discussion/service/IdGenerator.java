package org.rv.lab1.discussion.service;

import java.security.SecureRandom;

final class IdGenerator {
    private static final SecureRandom RND = new SecureRandom();

    private IdGenerator() {
    }

    static long nextId() {
        // 44 bits timestamp (ms) + 20 bits random -> fits into signed long and stays numeric for existing API
        long ts = System.currentTimeMillis() & ((1L << 44) - 1);
        long rnd = RND.nextInt(1 << 20);
        return (ts << 20) | rnd;
    }
}

