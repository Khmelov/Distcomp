package com.sergey.orsik.util;

import java.util.concurrent.ThreadLocalRandom;

public final class CommentIds {

    private CommentIds() {
    }

    /**
     * Time-ordered positive id with random low bits (sufficient for this coursework scope).
     */
    public static long newId() {
        long time = System.currentTimeMillis();
        int rnd = ThreadLocalRandom.current().nextInt(1, 1 << 20);
        long candidate = (time << 20) | rnd;
        return candidate > 0 ? candidate : candidate & Long.MAX_VALUE;
    }
}
