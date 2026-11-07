package com.distcomp.common.kafka;

/**
 * Operation requested by the publisher through the {@code InTopic}.
 */
public enum NoticeOperation {

    CREATE,
    GET,
    FIND_ALL,
    UPDATE,
    DELETE,
    DELETE_BY_ISSUE
}
