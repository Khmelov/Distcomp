package com.github.Lexya06.startrestapp.discussion.api.config;


import com.github.Lexya06.startrestapp.discussion.api.dto.notice.NoticeKeyDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToNoticeKeyDtoConverter implements Converter<String, NoticeKeyDto> {

    @Override
    public NoticeKeyDto convert(String source) {
        if (source == null || source.isBlank()) return null;

        String[] parts = source.split("_");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Неверный формат ключа. Ожидается: country_articleId_id");
        }

        return NoticeKeyDto.builder().country(parts[0]).articleId(Long.valueOf(parts[1])).id(Long.valueOf(parts[2])).build();
    }
}
