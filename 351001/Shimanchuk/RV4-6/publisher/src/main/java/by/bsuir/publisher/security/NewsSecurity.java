package by.bsuir.publisher.security;

import by.bsuir.publisher.dto.requests.NewsRequestDto;
import by.bsuir.publisher.repositories.NewsRepository;
import by.bsuir.publisher.repositories.WriterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("newsSecurity")
@RequiredArgsConstructor
public class NewsSecurity {

    private final NewsRepository newsRepository;
    private final WriterRepository writerRepository;

    public boolean isOwner(NewsRequestDto dto, String login) {
        if (dto == null || login == null) {
            return false;
        }
        if (dto.getWriterId() == null) {
            return false;
        }
        return writerRepository.findById(dto.getWriterId())
                .map(w -> login.equals(w.getLogin()))
                .orElse(false);
    }

    public boolean isOwnerById(Long newsId, String login) {
        if (newsId == null || login == null) {
            return false;
        }
        return newsRepository.findById(newsId)
                .map(n -> n.getWriter() != null && login.equals(n.getWriter().getLogin()))
                .orElse(false);
    }
}
