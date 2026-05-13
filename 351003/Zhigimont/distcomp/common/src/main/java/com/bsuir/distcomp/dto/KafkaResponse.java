package com.bsuir.distcomp.dto;

import lombok.Data;

@Data
public class KafkaResponse {
    private String correlationId;
    private String type;
    private Object payload;
    private String status;
}