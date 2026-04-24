package com.lizaveta.notebook.cache;

public final class NotebookCacheKeys {

    public static final String WRITER_PREFIX = "nb:cache:writer:";
    public static final String STORY_PREFIX = "nb:cache:story:";
    public static final String MARKER_PREFIX = "nb:cache:marker:";
    public static final String NOTICE_PREFIX = "nb:cache:notice:";

    private NotebookCacheKeys() {
    }

    public static String writerById(final long id) {
        return WRITER_PREFIX + "id:" + id;
    }

    public static String writerAll() {
        return WRITER_PREFIX + "all";
    }

    public static String writerPage(
            final int page,
            final int size,
            final String sortBy,
            final String sortOrder) {
        String sortField = sortBy == null || sortBy.isBlank() ? "_" : sortBy;
        String order = sortOrder == null || sortOrder.isBlank() ? "asc" : sortOrder.toLowerCase();
        return WRITER_PREFIX + "page:" + page + ":" + size + ":" + sortField + ":" + order;
    }

    public static String storyById(final long id) {
        return STORY_PREFIX + "id:" + id;
    }

    public static String storyAll() {
        return STORY_PREFIX + "all";
    }

    public static String storyPage(
            final int page,
            final int size,
            final String sortBy,
            final String sortOrder) {
        String sortField = sortBy == null || sortBy.isBlank() ? "_" : sortBy;
        String order = sortOrder == null || sortOrder.isBlank() ? "asc" : sortOrder.toLowerCase();
        return STORY_PREFIX + "page:" + page + ":" + size + ":" + sortField + ":" + order;
    }

    public static String markerById(final long id) {
        return MARKER_PREFIX + "id:" + id;
    }

    public static String markerAll() {
        return MARKER_PREFIX + "all";
    }

    public static String markerPage(
            final int page,
            final int size,
            final String sortBy,
            final String sortOrder) {
        String sortField = sortBy == null || sortBy.isBlank() ? "_" : sortBy;
        String order = sortOrder == null || sortOrder.isBlank() ? "asc" : sortOrder.toLowerCase();
        return MARKER_PREFIX + "page:" + page + ":" + size + ":" + sortField + ":" + order;
    }

    public static String markerByStory(final long storyId) {
        return MARKER_PREFIX + "story:" + storyId;
    }

    public static String noticeById(final long id) {
        return NOTICE_PREFIX + "id:" + id;
    }

    public static String noticeAll() {
        return NOTICE_PREFIX + "all";
    }

    public static String noticePage(
            final int page,
            final int size,
            final String sortBy,
            final String sortOrder) {
        String sortField = sortBy == null || sortBy.isBlank() ? "_" : sortBy;
        String order = sortOrder == null || sortOrder.isBlank() ? "asc" : sortOrder.toLowerCase();
        return NOTICE_PREFIX + "page:" + page + ":" + size + ":" + sortField + ":" + order;
    }

    public static String noticeByStory(final long storyId) {
        return NOTICE_PREFIX + "story:" + storyId;
    }
}
