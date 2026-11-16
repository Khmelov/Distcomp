package by.bsuir.rest.dto.request;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StoryMarkersDeserializer extends JsonDeserializer<List<MarkerRequestTo>> {

    @Override
    public List<MarkerRequestTo> deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        if (node == null || node.isNull()) {
            return null;
        }
        if (!node.isArray()) {
            return null;
        }
        List<MarkerRequestTo> markers = new ArrayList<>();
        for (JsonNode item : node) {
            MarkerRequestTo marker = new MarkerRequestTo();
            if (item.isTextual()) {
                marker.setName(item.asText());
            } else if (item.isNumber()) {
                marker.setId(item.asLong());
            } else if (item.isObject()) {
                if (item.hasNonNull("id")) {
                    marker.setId(item.get("id").asLong());
                }
                if (item.hasNonNull("name")) {
                    marker.setName(item.get("name").asText());
                }
            }
            markers.add(marker);
        }
        return markers;
    }
}
