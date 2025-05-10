package com.example.restservice.controller;

import com.example.restservice.dto.MarkerCreateDto;
import com.example.restservice.dto.MarkerResponseDto;
import com.example.restservice.mapper.MarkerMapper;
import com.example.restservice.model.Marker;
import com.example.restservice.repository.MarkerRepository;
import com.example.restservice.service.MarkerService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1.0")
@RequiredArgsConstructor
public class MarkerController {

    private final MarkerMapper markerMapper;
    private final MarkerRepository markerRepository;
    private final MarkerService markerService;

    private final RedisTemplate<String, Object> redisTemplate;
    ObjectMapper objectMapper = new ObjectMapper();
    private static final String CACHE_NAME = "markers";
    private static final long CACHE_TTL_MINUTES = 30;

    /*@Autowired
    public MarkerController(MarkerMapper markerMapper, MarkerRepository markerRepository, MarkerService markerService) {
        this.markerMapper = markerMapper;
        this.markerRepository = markerRepository;
        this.markerService = markerService;
    }*/
    @GetMapping("/markers")
    @ResponseStatus(HttpStatus.OK)
    public List<MarkerResponseDto> getMarkers() {
        String key = "allMarkers";
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            return objectMapper.convertValue(cached, new TypeReference<List<MarkerResponseDto>>() {});
        }

        List<MarkerResponseDto> markers = markerService.findAll().stream()
                .map(markerMapper::toDto)
                .collect(Collectors.toList());

        redisTemplate.opsForValue().set(key, markers, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        return markers;
    }

    @PostMapping("/markers")
    @ResponseStatus(HttpStatus.CREATED)
    public MarkerResponseDto createMarker(@RequestBody @Valid MarkerCreateDto inputDto) {
        Marker entity = markerMapper.toEntity(inputDto);
        Marker saved = markerService.save(entity);
        redisTemplate.delete("allMarkers");
        redisTemplate.opsForValue().set(
                "marker:" + inputDto.getId(),
                markerMapper.toDto(saved),
                CACHE_TTL_MINUTES,
                TimeUnit.MINUTES
        );
        return markerMapper.toDto(saved);
    }

    @GetMapping("/markers/{id}")
    public MarkerResponseDto getMarkerById(@PathVariable Long id) {
        String key = "marker:" + id;
        Object cached = redisTemplate.opsForValue().get(key);

        if (cached != null) {
            return objectMapper.convertValue(cached, MarkerResponseDto.class);
        }
        return markerMapper.toDto(markerService.findById(id));
    }

    @PutMapping("/markers")
    public ResponseEntity<MarkerResponseDto> updateMarker(@RequestBody @Valid MarkerCreateDto in) {
        redisTemplate.delete("allMarkers");
        redisTemplate.delete("marker:" + in.getId());
        redisTemplate.opsForValue().set(
                "marker:" + in.getId(),
                markerMapper.toDto(markerMapper.toEntity(in)),
                CACHE_TTL_MINUTES,
                TimeUnit.MINUTES
        );
        Marker marker = markerService.findById(in.getId());
        if (marker == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            Marker newMarker = markerMapper.toEntity(in);
            Marker updated = markerService.update(newMarker);
            return ResponseEntity.ok(markerMapper.toDto(updated));
        }
    }

    @DeleteMapping("/markers/{id}")
    public ResponseEntity<Void> deleteMarker(@PathVariable Long id) {
        redisTemplate.delete("allMarkers");
        redisTemplate.delete("marker:" + id);
        if (markerService.findById(id) != null) {
            markerService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
