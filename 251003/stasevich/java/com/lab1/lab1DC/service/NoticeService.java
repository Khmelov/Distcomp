package com.lab1.lab1DC.service;

import com.lab1.lab1DC.dto.NoticeRequestTo;
import com.lab1.lab1DC.dto.NoticeResponseTo;
import com.lab1.lab1DC.entity.Notice;
import com.lab1.lab1DC.repository.NoticeRepositoryJPA;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Not;
import org.springframework.stereotype.Service;

import java.io.Console;
import java.io.Writer;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepositoryJPA noticeRepositoryJPA;
    private boolean checkMult = false;
    public List<NoticeResponseTo> getAllNotices() {
        return noticeRepositoryJPA.findAll().stream()
                .map(notice -> new NoticeResponseTo(notice.getId(), notice.getContent(), notice.getStoryId()))
                .collect(Collectors.toList());
    }

    public NoticeResponseTo getNoticeById(Long id) {
        return noticeRepositoryJPA.findById(id).map(notice -> new NoticeResponseTo(notice.getId(), notice.getContent(), notice.getStoryId()))
                .orElseThrow(() -> new NoSuchElementException("Notice not found"));
    }

    @Transactional
    public NoticeResponseTo createNotice(Notice notice) {
        try {
            Notice saveN = noticeRepositoryJPA.save(notice);

        } catch (Exception e){
            System.out.println(e.toString());
        }
        return new NoticeResponseTo(notice.getId(), notice.getContent(), notice.getStoryId());
    }

    public NoticeResponseTo updateNotice(Long id, NoticeRequestTo request) {
        Notice notice = noticeRepositoryJPA.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Notice not found"));
        notice.setContent(request.getContent());
        notice.setStoryId(request.getStoryId());
        noticeRepositoryJPA.save(notice);
        return new NoticeResponseTo(id, notice.getContent(), notice.getStoryId());
    }

    public void deleteNotice(Long id) {
        Notice notice = noticeRepositoryJPA.findById(id).orElseThrow(() -> new NoSuchElementException("Notice not found"));
        noticeRepositoryJPA.deleteById(id);
    }
}
