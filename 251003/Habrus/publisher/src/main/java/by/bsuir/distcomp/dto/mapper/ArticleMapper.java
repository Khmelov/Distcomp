package by.bsuir.distcomp.dto.mapper;

import by.bsuir.distcomp.dto.request.ArticleRequestTo;
import by.bsuir.distcomp.dto.response.ArticleResponseTo;
import by.bsuir.distcomp.entity.Article;
import by.bsuir.distcomp.entity.Author;
import by.bsuir.distcomp.entity.Marker;
import by.bsuir.distcomp.repository.AuthorRepository;
import by.bsuir.distcomp.repository.MarkerRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class ArticleMapper {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private MarkerRepository markerRepository;

    @Mapping(target = "authorId", source = "author", qualifiedByName = "mapAuthorBack")
    public abstract ArticleResponseTo toDto(Article article);

    @Mapping(target = "author", source = "authorId", qualifiedByName = "mapAuthor")
    @Mapping(target = "markers", source = "markers", qualifiedByName = "mapMarkers")
    public abstract Article toEntity(ArticleRequestTo articleDTO);

    @Named("mapAuthor")
    protected Author mapAuthor(Long authorId) {
        return authorRepository
                .findById(authorId)
                .orElseThrow(() -> new NoSuchElementException("Author with id: " + authorId + " not found"));
    }

    @Named("mapAuthorBack")
    protected Long mapAuthorBack(Author author) {
        return author.getId();
    }

    @Named("mapMarkers")
    protected Set<Marker> mapMarkers(List<String> markerNames) {
        if (markerNames == null) return new HashSet<>();
        return markerNames.stream()
                .map(name -> markerRepository.findByName(name)
                        .orElseGet(() -> new Marker(null, name)))
                .collect(Collectors.toSet());
    }

}
