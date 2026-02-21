package com.github.Lexya06.startrestapp.model.repository.realization;

import com.github.Lexya06.startrestapp.model.entity.realization.Notice;
import com.github.Lexya06.startrestapp.model.repository.abstraction.MyCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class NoticeRepository extends MyCrudRepository<Notice> {
    HashMap<Long, Notice> noticeMap = new HashMap<>();
    @Override
    protected Map<Long, Notice> getBaseEntityMap() {
        return noticeMap;
    }
}
