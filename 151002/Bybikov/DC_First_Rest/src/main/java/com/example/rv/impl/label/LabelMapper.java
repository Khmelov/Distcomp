package com.example.rv.impl.label;
import java.util.List;

public interface LabelMapper {
    LabelRequestTo labelToRequestTo(Label label);

    List<LabelRequestTo> labelToRequestTo(Iterable<Label> labels);

    Label dtoToEntity(LabelRequestTo labelRequestTo);

    List<Label> dtoToEntity(Iterable<LabelRequestTo> labelRequestTos);

    LabelResponseTo labelToResponseTo(Label label);

    List<LabelResponseTo> labelToResponseTo(Iterable<Label> labels);
}
