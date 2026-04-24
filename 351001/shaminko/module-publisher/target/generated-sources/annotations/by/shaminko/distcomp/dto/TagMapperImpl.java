package by.shaminko.distcomp.dto;

import by.shaminko.distcomp.entities.Tag;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-24T13:24:46+0300",
    comments = "version: 1.6.1, compiler: javac, environment: Java 21.0.9 (Ubuntu)"
)
@Component
public class TagMapperImpl implements TagMapper {

    @Override
    public TagResponseTo out(Tag editor) {
        if ( editor == null ) {
            return null;
        }

        TagResponseTo tagResponseTo = new TagResponseTo();

        tagResponseTo.setId( editor.getId() );
        tagResponseTo.setName( editor.getName() );

        return tagResponseTo;
    }

    @Override
    public Tag in(TagRequestTo editor) {
        if ( editor == null ) {
            return null;
        }

        Tag.TagBuilder tag = Tag.builder();

        tag.id( editor.getId() );
        tag.name( editor.getName() );

        return tag.build();
    }
}
