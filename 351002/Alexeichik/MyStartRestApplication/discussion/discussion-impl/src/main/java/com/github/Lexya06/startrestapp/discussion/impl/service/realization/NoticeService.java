package com.github.Lexya06.startrestapp.discussion.impl.service.realization;

import com.github.Lexya06.startrestapp.discussion.api.dto.notice.NoticeKeyDto;
import com.github.Lexya06.startrestapp.discussion.api.dto.notice.NoticeRequestTo;
import com.github.Lexya06.startrestapp.discussion.api.dto.notice.NoticeResponseTo;
import com.github.Lexya06.startrestapp.discussion.api.searchcriteria.implementation.NoticeSearchCriteria;
import com.github.Lexya06.startrestapp.discussion.impl.model.entity.realization.Notice;
import com.github.Lexya06.startrestapp.discussion.impl.model.entity.realization.NoticeKey;
import com.github.Lexya06.startrestapp.discussion.impl.model.repository.impl.MyCrudRepositoryImpl;
import com.github.Lexya06.startrestapp.discussion.impl.model.repository.realization.NoticeRepository;
import com.github.Lexya06.startrestapp.discussion.impl.service.abstraction.BaseEntityService;
import com.github.Lexya06.startrestapp.discussion.impl.service.customexception.MyEntityNotFoundException;
import com.github.Lexya06.startrestapp.discussion.impl.service.mapper.impl.GenericKeyMapperImpl;
import com.github.Lexya06.startrestapp.discussion.impl.service.mapper.impl.GenericMapperImpl;
import com.github.Lexya06.startrestapp.discussion.impl.service.mapper.realization.NoticeKeyMapper;
import com.github.Lexya06.startrestapp.discussion.impl.service.mapper.realization.NoticeMapper;
import io.hypersistence.tsid.TSID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
public class NoticeService extends BaseEntityService<Notice, NoticeKey, NoticeKeyDto, NoticeRequestTo, NoticeResponseTo, NoticeSearchCriteria> {
    final NoticeRepository noticeRepository;
    final NoticeMapper noticeMapper;
    final NoticeKeyMapper noticeKeyMapper;

    @Autowired
    public NoticeService(NoticeRepository noticeRepository, NoticeMapper noticeMapper, NoticeKeyMapper noticeKeyMapper) {
        super(Notice.class);
        this.noticeRepository = noticeRepository;
        this.noticeMapper = noticeMapper;
        this.noticeKeyMapper = noticeKeyMapper;
    }

    @Override
    protected MyCrudRepositoryImpl<Notice, NoticeKey> getRepository() {
        return noticeRepository;
    }

    @Override
    protected GenericMapperImpl<Notice, NoticeKey, NoticeRequestTo, NoticeResponseTo> getMapper() {
        return noticeMapper;
    }

    @Override
    protected GenericKeyMapperImpl<NoticeKey, NoticeKeyDto> getKeyMapper() {
        return noticeKeyMapper;
    }

    @Override
    protected Slice<Notice> getEntitySlice(NoticeSearchCriteria criteria) {
        Pageable pageable = createPageable(criteria.getPagingState(), criteria.getSize());
        String country = criteria.getCountry();
        Long articleId = criteria.getArticleId();
        if (country != null && articleId != null) {
            return noticeRepository.findByCountryAndArticleId(country, articleId, pageable);
        } else if (country != null) {
            return noticeRepository.findByCountry(country, pageable);
        } else if (articleId != null) {
            return noticeRepository.findByArticleId(articleId, pageable);
        }
        return noticeRepository.findAll(pageable);
    }

    @Override
    public NoticeResponseTo createEntity(NoticeRequestTo requestDTO) {
        Notice entity = noticeMapper.createEntityFromRequest(requestDTO);
        entity.getId().setId(TSID.Factory.getTsid().toLong());
        entity = noticeRepository.save(entity);
        return noticeMapper.createResponseFromEntity(entity);
    }

    public void deleteEntityByIdId(Long id) {
        Notice entity = noticeRepository.findByIdId(id).orElseThrow(()->new MyEntityNotFoundException(id.toString(), getEntityClass()));
        noticeRepository.delete(entity);
    }

    public NoticeResponseTo getEntityByIdId(Long id) {
        Notice entity = noticeRepository.findByIdId(id).orElseThrow(()->new MyEntityNotFoundException(id.toString(), getEntityClass()));
        return getMapper().createResponseFromEntity(entity);
    }

    public NoticeResponseTo updateEntityByIdId(Long id, NoticeRequestTo requestDTO) {
        Notice entity = noticeRepository.findByIdId(id).orElseThrow(()->new MyEntityNotFoundException(id.toString(), getEntityClass()));
        getMapper().updateEntityFromRequest(requestDTO, entity);
        entity = getRepository().save(entity);
        return getMapper().createResponseFromEntity(entity);
    }



}
