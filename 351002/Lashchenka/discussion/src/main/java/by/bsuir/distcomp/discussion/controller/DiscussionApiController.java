package by.bsuir.distcomp.discussion.controller;

import by.bsuir.distcomp.discussion.api.DiscussionApiPaths;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping(value = DiscussionApiPaths.API_V1, produces = MediaType.APPLICATION_JSON_VALUE)
public class DiscussionApiController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> apiV1Root() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("service", "distcomp-discussion");
        body.put("port", 24130);
        body.put("resources", Map.of(
                "reactions", DiscussionApiPaths.REACTIONS,
                "reactionsDescription", "GET/POST collection; GET/PUT/DELETE by id"
        ));
        return ResponseEntity.ok(body);
    }
}
