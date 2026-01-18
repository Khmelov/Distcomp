package org.discussion.service;

import org.discussion.model.NoteKey;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CompositeIdCodec {

    private static final Map<String, Integer> COUNTRY_TO_CODE = Map.of(
            "RU", 1,
            "US", 2,
            "DE", 3,
            "FR", 4,
            "GB", 5,
            "CN", 6,
            "JP", 7,
            "BR", 8
    );
    private static final Map<Integer, String> CODE_TO_COUNTRY = COUNTRY_TO_CODE
            .entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey)
            );

    // Размер полей в битах
    private static final int COUNTRY_BITS = 8;   // 256 стран хватит за глаза
    private static final int ISSUE_ID_BITS = 28; // до 268 млн
    private static final int ID_BITS = 28;       // до 268 млн

    // Маски
    private static final long COUNTRY_MASK = (1L << COUNTRY_BITS) - 1;
    private static final long ISSUE_ID_MASK = (1L << ISSUE_ID_BITS) - 1;
    private static final long ID_MASK = (1L << ID_BITS) - 1;

    public long encode(String country, long issueId, long id) {
        int countryCode = COUNTRY_TO_CODE
                .getOrDefault(
                        country.trim().toUpperCase(),
                        0
                );
        return ((long) countryCode << (ISSUE_ID_BITS + ID_BITS))           // country — старшие 8 бит
                | ((issueId & ISSUE_ID_MASK) << ID_BITS)                     // issueId — следующие 28 бит
                | (id & ID_MASK);                                            // id — младшие 28 бит
    }

    public NoteKey decode(long compositeId) {
        int countryCode = (int) ((compositeId >>> (ISSUE_ID_BITS + ID_BITS)) & COUNTRY_MASK);
        long issueId = (compositeId >>> ID_BITS) & ISSUE_ID_MASK;
        long id = compositeId & ID_MASK;

        String country = CODE_TO_COUNTRY.getOrDefault(countryCode, "UNKNOWN");

        return new NoteKey(country, issueId, id);
    }
}