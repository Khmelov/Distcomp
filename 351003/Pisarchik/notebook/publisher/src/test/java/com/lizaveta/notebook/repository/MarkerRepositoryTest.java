package com.lizaveta.notebook.repository;

import com.lizaveta.notebook.config.PostgresTestContainerConfig;
import com.lizaveta.notebook.model.entity.Marker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest
class MarkerRepositoryTest extends PostgresTestContainerConfig {

    @Autowired
    private MarkerRepository markerRepository;

    @Test
    void save_ShouldPersistMarker() {
        Marker marker = new Marker(null, "fantasy");
        Marker saved = markerRepository.save(marker);
        assertThat(saved.getId(), notNullValue());
        assertThat(saved.getName(), equalTo("fantasy"));
    }

    @Test
    void findById_WhenExists_ShouldReturnMarker() {
        Marker marker = new Marker(null, "adventure");
        Marker saved = markerRepository.save(marker);
        Optional<Marker> found = markerRepository.findById(saved.getId());
        assertThat(found.isPresent(), equalTo(true));
        assertThat(found.get().getName(), equalTo("adventure"));
    }

    @Test
    void findById_WhenNotExists_ShouldReturnEmpty() {
        Optional<Marker> found = markerRepository.findById(99999L);
        assertThat(found.isEmpty(), equalTo(true));
    }

    @Test
    void findAll_WithPageable_ShouldReturnPage() {
        markerRepository.save(new Marker(null, "m1"));
        markerRepository.save(new Marker(null, "m2"));
        var page = markerRepository.findAll(PageRequest.of(0, 1, Sort.by("id")));
        assertThat(page.getContent(), hasSize(1));
        assertThat(page.getTotalElements(), equalTo(2L));
        assertThat(page.getTotalPages(), equalTo(2));
    }

    @Test
    void update_ShouldModifyMarker() {
        Marker saved = markerRepository.save(new Marker(null, "old-name"));
        Marker updated = saved.withName("new-name");
        Marker result = markerRepository.update(updated);
        assertThat(result.getName(), equalTo("new-name"));
        Optional<Marker> found = markerRepository.findById(saved.getId());
        assertThat(found.get().getName(), equalTo("new-name"));
    }

    @Test
    void deleteById_WhenExists_ShouldReturnTrue() {
        Marker saved = markerRepository.save(new Marker(null, "to-delete"));
        boolean deleted = markerRepository.deleteById(saved.getId());
        assertThat(deleted, equalTo(true));
        assertThat(markerRepository.findById(saved.getId()).isEmpty(), equalTo(true));
    }

    @Test
    void deleteById_WhenNotExists_ShouldReturnFalse() {
        boolean deleted = markerRepository.deleteById(99999L);
        assertThat(deleted, equalTo(false));
    }

    @Test
    void existsById_WhenExists_ShouldReturnTrue() {
        Marker saved = markerRepository.save(new Marker(null, "exists"));
        assertThat(markerRepository.existsById(saved.getId()), equalTo(true));
    }

    @Test
    void existsById_WhenNotExists_ShouldReturnFalse() {
        assertThat(markerRepository.existsById(99999L), equalTo(false));
    }
}
