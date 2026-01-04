package com.blog.model;

public enum MessageState {
    PENDING("PENDING"),
    APPROVED("APPROVED"),  // В логах видно "APPROVED", а не "APPROVE"
    DECLINED("DECLINED");  // В логах видно "DECLINED", а не "DECLINE"

    private final String value;

    MessageState(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static MessageState fromString(String value) {
        for (MessageState state : MessageState.values()) {
            if (state.getValue().equalsIgnoreCase(value)) {
                return state;
            }
        }
        // Пробуем альтернативные варианты
        if ("APPROVE".equalsIgnoreCase(value)) {
            return APPROVED;
        }
        if ("DECLINE".equalsIgnoreCase(value)) {
            return DECLINED;
        }
        throw new IllegalArgumentException("Unknown MessageState: " + value);
    }
}

