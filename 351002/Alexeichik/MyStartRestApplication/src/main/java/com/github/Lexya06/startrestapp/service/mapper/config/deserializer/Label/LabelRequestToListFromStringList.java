package com.github.Lexya06.startrestapp.service.mapper.config.deserializer.Label;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.Lexya06.startrestapp.model.dto.request.LabelRequestTo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LabelRequestToListFromStringList extends JsonDeserializer<List<LabelRequestTo>> {
    @Override
    public List<LabelRequestTo> deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {
        List<LabelRequestTo> labels = new ArrayList<>();

        if (p.currentToken() == JsonToken.START_ARRAY) {
            while (p.nextToken() != JsonToken.END_ARRAY) {
                if (p.currentToken() == JsonToken.VALUE_STRING) {
                    labels.add(LabelRequestTo.builder()
                            .name(p.getText())
                            .build());
                }
            }
        }

        return labels;
    }
}
