package com.example.discussion.config;

import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperConfig(
        componentModel = "spring", // Указывает, что мапперы должны быть Spring-бинами
        unmappedTargetPolicy = ReportingPolicy.IGNORE // Игнорировать непроставленные поля
)
public class MapStructConfig {
    // Дополнительные настройки, если необходимо
}