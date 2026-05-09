package com.tweetservice.configs.markerclientconfig;

import com.tweetservice.dtos.marker.MarkerResponseTo;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

@HttpExchange("/api/v1.0")
public interface MarkerClient {

    @GetExchange("/markers")
    List<MarkerResponseTo> getMarkersByIds(@RequestParam List<Long> ids);

    @DeleteExchange("/markers/ids")
    void deleteMarkersByIds(@RequestBody List<Long> ids);
}
