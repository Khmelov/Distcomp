package com.github.Lexya06.startrestapp.model.repository.realization;

import com.github.Lexya06.startrestapp.model.entity.realization.Notice;
import com.github.Lexya06.startrestapp.model.repository.impl.MyCrudRepositoryImpl;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public interface NoticeRepository extends MyCrudRepositoryImpl<Notice> {
}
