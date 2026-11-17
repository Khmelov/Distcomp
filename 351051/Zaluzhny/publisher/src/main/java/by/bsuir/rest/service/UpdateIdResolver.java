package by.bsuir.rest.service;

import by.bsuir.rest.exception.ApiException;
import by.bsuir.rest.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class UpdateIdResolver {

    public Long resolve(Long pathId, Long bodyId) {
        if (pathId != null && bodyId != null && !pathId.equals(bodyId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, ErrorCode.ID_MISMATCH,
                    "Path id and body id do not match");
        }
        Long id = pathId != null ? pathId : bodyId;
        if (id == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, ErrorCode.ID_REQUIRED, "id is required");
        }
        return id;
    }
}
