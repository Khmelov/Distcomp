    package com.example.distcomp_1.mdoel;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

    @Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
public class News {
    Long id;
    Long authorId;
    String title;
    String content;
    Date created;
    Date modified;

    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PUBLIC)
    public static class In{
        @Positive
        @Nullable
        Long id;
        @Positive
        Long authorId;
        @Size(min=2, max=64)
        String title;
        @Size(min=4, max=2048)
        String content;
        Date created;
        Date modified;
    }

    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PUBLIC)
    public static class Out{
        Long id;
        Long authorId;
        String title;
        String content;
        Date created;
        Date modified;
    }


}
