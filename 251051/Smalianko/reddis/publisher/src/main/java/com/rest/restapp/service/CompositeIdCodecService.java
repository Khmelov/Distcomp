package com.rest.restapp.service;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CompositeIdCodecService {

    private static final Map<String, Integer> COUNTRY_TO_CODE = Map.of(
            "RU", 1, "US", 2, "DE", 3, "FR", 4, "GB", 5, "CN", 6, "JP", 7, "BR", 8
    );

    private static final int ISSUE_ID_BITS = 28; // до 268 млн
    private static final int ID_BITS = 28;       // до 268 млн

    private static final long ISSUE_ID_MASK = (1L << ISSUE_ID_BITS) - 1;
    private static final long ID_MASK = (1L << ID_BITS) - 1;

    public long encode(String country, long issueId, long id) {
        int countryCode = COUNTRY_TO_CODE.getOrDefault(country.trim().toUpperCase(), 0);

        return ((long) countryCode << (ISSUE_ID_BITS + ID_BITS))           // country — старшие 8 бит
                | ((issueId & ISSUE_ID_MASK) << ID_BITS)                     // issueId — следующие 28 бит
                | (id & ID_MASK);                                            // id — младшие 28 бит
    }
}