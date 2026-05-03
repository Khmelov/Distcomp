package by.shaminko.distcomp.kafka;

import by.shaminko.distcomp.dto.ReactionRequestTo;
import by.shaminko.distcomp.dto.ReactionResponseTo;

import java.util.List;

public record MessageData(
        String correlationId,
        String partitionKey,
        Operation operation,
        Long itemId,
        ReactionRequestTo requestTO,
        List<ReactionResponseTo> responseTOs,
        ExceptionData exception
) {
    public MessageData(Operation operation){
        this(null, null, operation, null, null, null, null);
    }
    public MessageData(Operation operation, Long itemId) {
        this(null, null, operation, itemId, null, null, null);
    }
    public MessageData(Operation operation, ReactionRequestTo requestTO){
        this(null, null, operation, null, requestTO, null, null);
    }
    public MessageData(Operation operation, List<ReactionResponseTo> responseTOs){
        this(null, null, operation, null, null, responseTOs, null);
    }
    public MessageData(ExceptionData exception){
        this(null, null, Operation.EXCEPTION, null, null, null, exception);
    }
    public MessageData withRouting(String correlationId, String partitionKey) {
        return new MessageData(correlationId, partitionKey, operation, itemId, requestTO, responseTOs, exception);
    }
    public enum Operation{
        GET_ALL,
        GET_BY_ID,
        CREATE,
        UPDATE,
        DELETE_BY_ID,
        EXCEPTION
    }
    public record ExceptionData(
            String simpleName,
            String message
    ){}
}
