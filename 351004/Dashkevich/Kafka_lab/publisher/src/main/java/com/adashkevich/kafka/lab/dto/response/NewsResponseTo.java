package com.adashkevich.kafka.lab.dto.response;

import java.util.Set;


public class NewsResponseTo {
    public Long id;
    public Long editorId;
    public String title;
    public String content;
    public String created;   // ISO8601
    public String modified;  // ISO8601
    public Set<Long> markerIds;
}
