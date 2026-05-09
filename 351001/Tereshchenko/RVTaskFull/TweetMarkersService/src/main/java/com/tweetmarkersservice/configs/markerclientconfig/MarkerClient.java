package com.tweetmarkersservice.configs.markerclientconfig;

import com.tweetmarkersservice.dtos.MarkerResponseByNameTo;
import com.tweetmarkersservice.dtos.TweetMarkersRequestByNameTo;
import com.tweetmarkersservice.dtos.marker.MarkerResponseTo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.List;

@HttpExchange("/api/v1.0")
public interface MarkerClient {

    @GetExchange("/markers")
    public List<MarkerResponseTo> getMarkersByIds(@RequestParam List<Long> ids);

    @PostExchange("/markers/markersNames")
    public List<MarkerResponseByNameTo> getMarkersByNames(@RequestBody List<TweetMarkersRequestByNameTo> requestList);
}
