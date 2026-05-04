package by.bsuir.task310.mapper;

import by.bsuir.task310.dto.LabelRequestTo;
import by.bsuir.task310.dto.LabelResponseTo;
import by.bsuir.task310.model.Label;
import org.springframework.stereotype.Component;

@Component
public class LabelMapper {

    public Label toEntity(LabelRequestTo requestTo) {
        Label label = new Label();
        label.setId(requestTo.getId());
        label.setName(requestTo.getName());
        return label;
    }

    public LabelResponseTo toResponseTo(Label label) {
        LabelResponseTo responseTo = new LabelResponseTo();
        responseTo.setId(label.getId());
        responseTo.setName(label.getName());
        return responseTo;
    }
}