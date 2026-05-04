package com.example.demo.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
public class StringDeserializer extends StdDeserializer<String> {
    public StringDeserializer() {
        super(String.class);
    }

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.currentToken() == JsonToken.VALUE_STRING) {
            return p.getValueAsString();
        } else if (p.currentToken() == JsonToken.VALUE_NUMBER_INT) {
            throw new IOException("Content must be a string, not a number");
        } else {
            throw new IOException("Content must be a string");
        }
    }
}
