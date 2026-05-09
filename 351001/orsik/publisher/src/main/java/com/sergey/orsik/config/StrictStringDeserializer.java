package com.sergey.orsik.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import java.io.IOException;

public class StrictStringDeserializer extends JsonDeserializer<String> {
    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        // Проверяем текущий токен. Если это не строка (VALUE_STRING) — кидаем ошибку.
        if (p.hasToken(JsonToken.VALUE_NUMBER_INT) || p.hasToken(JsonToken.VALUE_NUMBER_FLOAT)) {
            throw MismatchedInputException.from(p, String.class, "Числа запрещены в этом поле, нужна строка");
        }

        // Если это не строка и не число (например, объект или массив)
        if (!p.hasToken(JsonToken.VALUE_STRING)) {
            throw MismatchedInputException.from(p, String.class, "Ожидалась строка, получено: " + p.currentToken());
        }

        return p.getText();
    }
}
