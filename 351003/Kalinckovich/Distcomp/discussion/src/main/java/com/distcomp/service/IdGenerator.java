package com.distcomp.service;

import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator {
    private static final AtomicLong counter = new AtomicLong(1);

    public static long nextId() {
        return counter.getAndIncrement();
    }
}