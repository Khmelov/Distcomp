package by.boukhvalova.distcomp.kafka;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@Getter
@Setter
public class KafkaException extends RuntimeException {
    public KafkaException(String exceptionSimpleName, String message) {
        super(message);
        simpleName = exceptionSimpleName;
    }

    private String simpleName;

}
