package org.ex.distributed_computing.dto.response;

public record AuthorResponseDTO(
    Long id,
    String login,
    String password,
    String firstname,
    String lastname
) {

}
