package com.example.rv.impl.label;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class LabelMapperImpl implements LabelMapper {
    @Override
    public LabelRequestTo labelToRequestTo(Label label) {
        return new LabelRequestTo(
                label.getId(),
                label.getName()
        );
    }

    @Override
    public List<LabelRequestTo> labelToRequestTo(Iterable<Label> labels) {
        return StreamSupport.stream(labels.spliterator(), false)
                .map(this::labelToRequestTo)
                .collect(Collectors.toList());
    }

    @Override
    public Label dtoToEntity(LabelRequestTo labelRequestTo) {
        return new Label(
                labelRequestTo.id(),
                labelRequestTo.name()
        );
    }

    @Override
    public List<Label> dtoToEntity(Iterable<LabelRequestTo> labelRequestTos) {
        return StreamSupport.stream(labelRequestTos.spliterator(), false)
                .map(this::dtoToEntity)
                .collect(Collectors.toList());
    }

    @Override
    public LabelResponseTo labelToResponseTo(Label label) {
        return new LabelResponseTo(
                label.getId(),
                label.getName()
        );
    }

    @Override
    public List<LabelResponseTo> labelToResponseTo(Iterable<Label> labels) {
        return StreamSupport.stream(labels.spliterator(), false)
                .map(this::labelToResponseTo)
                .collect(Collectors.toList());
    }
}
