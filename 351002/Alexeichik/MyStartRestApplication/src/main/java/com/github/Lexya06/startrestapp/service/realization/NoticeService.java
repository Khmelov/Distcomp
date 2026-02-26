package com.github.Lexya06.startrestapp.service.realization;

import com.github.Lexya06.startrestapp.model.dto.request.NoticeRequestTo;
import com.github.Lexya06.startrestapp.model.dto.response.NoticeResponseTo;
import com.github.Lexya06.startrestapp.model.entity.realization.Article;
import com.github.Lexya06.startrestapp.model.entity.realization.Notice;
import com.github.Lexya06.startrestapp.model.repository.abstraction.MyCrudRepository;
import com.github.Lexya06.startrestapp.model.repository.realization.ArticleRepository;
import com.github.Lexya06.startrestapp.model.repository.realization.NoticeRepository;
import com.github.Lexya06.startrestapp.service.abstraction.BaseEntityService;
import com.github.Lexya06.startrestapp.service.customexception.MyEntityNotFoundException;
import com.github.Lexya06.startrestapp.service.mapper.impl.GenericMapperImpl;
import com.github.Lexya06.startrestapp.service.mapper.realization.NoticeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NoticeService extends BaseEntityService<Notice, NoticeRequestTo, NoticeResponseTo> {
    NoticeRepository noticeRepository;
    ArticleRepository articleRepository;
    NoticeMapper noticeMapper;

    @Autowired
    public NoticeService(NoticeRepository noticeRepository, ArticleRepository articleRepository, NoticeMapper noticeMapper) {
        super(Notice.class);
        this.noticeRepository = noticeRepository;
        this.articleRepository = articleRepository;
        this.noticeMapper = noticeMapper;
    }

    @Override
    protected MyCrudRepository<Notice> getRepository() {
        return noticeRepository;
    }

    @Override
    protected GenericMapperImpl<Notice, NoticeRequestTo, NoticeResponseTo> getMapper() {
        return noticeMapper;
    }

    @Override
    public NoticeResponseTo createEntity(NoticeRequestTo noticeRequestTo){
        Article article = articleRepository.findById(noticeRequestTo.getArticleId()).orElseThrow(()->new MyEntityNotFoundException(noticeRequestTo.getArticleId(), Article.class));
        Notice notice = noticeMapper.createEntityFromRequest(noticeRequestTo);
        notice.setArticle(article);
        notice = noticeRepository.save(notice);
        return noticeMapper.createResponseFromEntity(notice);
    }
}
