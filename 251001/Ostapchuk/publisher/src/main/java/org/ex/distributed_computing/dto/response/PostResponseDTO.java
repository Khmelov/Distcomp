package org.ex.distributed_computing.dto.response;

import java.io.Serializable;

public record PostResponseDTO(Long id, Long tweetId, String content) implements Serializable {}

