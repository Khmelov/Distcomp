package by.yelkin.TopicService.service;

import by.yelkin.TopicService.dto.mark.MarkRq;
import by.yelkin.TopicService.dto.mark.MarkRs;
import by.yelkin.TopicService.dto.mark.MarkUpdateRq;
import by.yelkin.TopicService.exception.ApiError;
import by.yelkin.TopicService.exception.ApiException;
import by.yelkin.TopicService.mapping.MarkMapper;
import by.yelkin.TopicService.repository.MarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MarkService {
    private final MarkRepository markRepository;
    private final MarkMapper markMapper;

    public MarkRs create(MarkRq rq) {
        return markMapper.toDto(markRepository.save(markMapper.fromDto(rq)));
    }

    public MarkRs readById(Long id) {
        return markMapper.toDto(markRepository.findById(id)
                .orElseThrow(() -> new ApiException(ApiError.ERR_MARK_NOT_FOUND, id.toString())));
    }

    public List<MarkRs> readAll() {
        return markMapper.toDtoList(markRepository.findAll());
    }

    public MarkRs update(MarkUpdateRq rq) {
        var mark = markRepository.findById(rq.getId())
                .orElseThrow(() -> new ApiException(ApiError.ERR_MARK_NOT_FOUND, rq.getId().toString()));

        markMapper.updateMark(mark, rq);

        return markMapper.toDto(markRepository.save(mark));
    }

    public void deleteById(Long id) {
        if (markRepository.findById(id).isEmpty()) {
            throw new ApiException(ApiError.ERR_MARK_NOT_FOUND, id.toString());
        }
        markRepository.deleteById(id);
    }
}
