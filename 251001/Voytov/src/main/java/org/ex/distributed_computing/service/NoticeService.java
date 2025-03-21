package org.ex.distributed_computing.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.ex.distributed_computing.dto.request.NoticeRequestDTO;
import org.ex.distributed_computing.dto.response.NoticeResponseDTO;
import org.ex.distributed_computing.exception.NotFoundException;
import org.ex.distributed_computing.mapper.NoticeMapper;
import org.ex.distributed_computing.model.News;
import org.ex.distributed_computing.model.Notice;
import org.ex.distributed_computing.repository.NewsRepository;
import org.ex.distributed_computing.repository.NoticeRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoticeService {

  private final NoticeRepository noticeRepository;
  private final NewsRepository newsRepository;
  private final NoticeMapper noticeMapper;

  public List<NoticeResponseDTO> getAllNotices() {
    return noticeMapper.toDtoList(noticeRepository.findAll());
  }

  public NoticeResponseDTO getNoticeById(Long id) {
    Notice notice = noticeRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Notice not found"));
    return noticeMapper.toDto(notice);
  }

  public NoticeResponseDTO createNotice(NoticeRequestDTO requestDTO) {
    News news = newsRepository.findById(requestDTO.newsId())
        .orElseThrow(() -> new NotFoundException("News not found"));

    Notice notice = new Notice(news, requestDTO.content());
    noticeRepository.save(notice);
    return noticeMapper.toDto(notice);
  }

  public NoticeResponseDTO updateNotice(NoticeRequestDTO requestDTO) {
    Notice notice = noticeRepository.findById(requestDTO.id())
        .orElseThrow(() -> new NotFoundException("Notice not found"));

    News news = newsRepository.findById(requestDTO.newsId())
        .orElseThrow(() -> new NotFoundException("News not found"));

    notice.setNews(news);
    notice.setContent(requestDTO.content());
    noticeRepository.save(notice);
    return noticeMapper.toDto(notice);
  }

  public void deleteNotice(Long id) {
    Notice notice = noticeRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Notice not found"));
    noticeRepository.delete(notice);
  }
}

