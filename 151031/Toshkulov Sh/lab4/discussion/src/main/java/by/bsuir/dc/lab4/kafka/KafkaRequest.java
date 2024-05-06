package by.bsuir.dc.lab4.kafka;

import by.bsuir.dc.lab4.dto.CommentRequestTo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KafkaRequest {

    private UUID key;

    private Methods requestMethod;

    private List<CommentRequestTo> dtoToTransfer = new ArrayList<>();


}
