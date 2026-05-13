package by.bsuir.publisher.services.impl;

import by.bsuir.publisher.dto.requests.CommentRequestDto;
import by.bsuir.publisher.dto.responses.CommentResponseDto;
import by.bsuir.publisher.exceptions.EntityExistsException;
import by.bsuir.publisher.exceptions.Comments;
import by.bsuir.publisher.exceptions.NoEntityExistsException;
import by.bsuir.publisher.services.CommentService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated
public class CommentServiceImpl implements CommentService {
    private final RestTemplate restTemplate;
    private final static String CREATE_MESSAGE_URI = "http://localhost:24130/api/v1.0/comments";
    private final static String READ_MESSAGE_URI = "http://localhost:24130/api/v1.0/comments/";
    private final static String READ_ALL_MESSAGE_URI = "http://localhost:24130/api/v1.0/comments";
    private final static String UPDATE_MESSAGE_URI = "http://localhost:24130/api/v1.0/comments";
    private final static String DELETE_MESSAGE_URI = "http://localhost:24130/api/v1.0/comments/";
    @Override
    public CommentResponseDto create(@NonNull CommentRequestDto dto) throws EntityExistsException {
        try {
            return restTemplate.postForObject(CREATE_MESSAGE_URI, dto, CommentResponseDto.class);
        } catch (HttpClientErrorException e) {
            throw new EntityExistsException(Comments.EntityExistsException);
        }
    }

    @Override
    public Optional<CommentResponseDto> read(@NonNull Long uuid) {
        try {
            return Optional.ofNullable(restTemplate.getForObject(READ_MESSAGE_URI + uuid,
                    CommentResponseDto.class));
        } catch(HttpClientErrorException e) {
            return Optional.empty();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CommentResponseDto> readAll() {
        return restTemplate.getForObject(READ_ALL_MESSAGE_URI, List.class);
    }

    @Override
    public CommentResponseDto update(@NonNull CommentRequestDto dto) throws NoEntityExistsException {
        try {
            return restTemplate.exchange(UPDATE_MESSAGE_URI, HttpMethod.PUT,
                    new HttpEntity<>(dto), CommentResponseDto.class).getBody();
        } catch (HttpClientErrorException e) {
            throw new NoEntityExistsException(Comments.NoEntityExistsException);
        }
    }

    @Override
    public Long delete(@NonNull Long uuid) throws NoEntityExistsException {
        try {
            return restTemplate.exchange(DELETE_MESSAGE_URI + uuid, HttpMethod.DELETE,
                    new HttpEntity<>(uuid), Long.class).getBody();
        }
         catch (HttpClientErrorException e) {
            throw new NoEntityExistsException(Comments.NoEntityExistsException);
        }
    }
}
