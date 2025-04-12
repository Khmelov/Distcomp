package by.ryabchikov.tweet_service.util;

import by.ryabchikov.tweet_service.dto.mark.MarkRequestTo;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.List;

public class MarkRequestToDeserializer extends JsonDeserializer<List<MarkRequestTo>> {

    @Override
    public List<MarkRequestTo> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        List<String> markNames = jsonParser.readValueAs(List.class);
        return markNames.stream()
                .map(name -> MarkRequestTo.builder().name(name).build())
                .toList();
    }
}
