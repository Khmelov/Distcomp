
package com.example.task310.service;

import com.example.task310.dto.MarkerRequestTo;
import com.example.task310.dto.MarkerResponseTo;
import com.example.task310.model.Marker;
import com.example.task310.repository.MarkerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// Сервисный слой содержит бизнес-логику работы с маркерами.
// Получает запросы от MarkerController, взаимодействует с MarkerRepository
// и преобразует объекты модели Marker в DTO для передачи клиенту.
@Service
public class MarkerService {

    // Репозиторий отвечает за хранение, поиск и удаление маркеров.
    private final MarkerRepository markerRepository;

    // Spring передаёт репозиторий через конструктор (внедрение зависимости).
    public MarkerService(MarkerRepository markerRepository) {
        this.markerRepository = markerRepository;
    }

    // Получение списка всех маркеров.
    // Repository возвращает модели Marker.
    // Каждая модель преобразуется в MarkerResponseTo с помощью toResponse().
    public List<MarkerResponseTo> findAll() {
        return markerRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Поиск маркера по идентификатору.
    // Если запись найдена, модель преобразуется в DTO ответа.
    // Если маркер отсутствует, возвращается Optional.empty().
    public Optional<MarkerResponseTo> findById(Long id) {
        return markerRepository.findById(id)
                .map(this::toResponse);
    }

    // Поиск маркера по названию.
    // Repository выполняет поиск по полю name.
    // Найденная модель преобразуется в MarkerResponseTo.
    public Optional<MarkerResponseTo> findByName(String name) {
        return markerRepository.findByName(name)
                .map(this::toResponse);
    }

    // Создание нового маркера.
    // На вход поступает MarkerRequestTo с названием маркера.
    // Создаётся модель Marker, в которую записывается полученное название.
    // Repository сохраняет модель и возвращает её с присвоенным идентификатором.
    // Сохранённая модель преобразуется в DTO ответа.
    public MarkerResponseTo save(MarkerRequestTo request) {
        Marker marker = new Marker();
        marker.setName(request.getName());

        Marker savedMarker = markerRepository.save(marker);

        return toResponse(savedMarker);
    }

    // Обновление существующего маркера.
    // Сначала проверяется наличие записи по идентификатору.
    // Если маркер не найден, возвращается Optional.empty().
    // Если запись существует, изменяется её название.
    // Repository сохраняет обновлённую модель,
    // после чего она преобразуется в MarkerResponseTo.
    public Optional<MarkerResponseTo> update(Long id, MarkerRequestTo request) {
        Optional<Marker> existingMarker = markerRepository.findById(id);

        if (existingMarker.isEmpty()) {
            return Optional.empty();
        }

        Marker marker = existingMarker.get();
        marker.setName(request.getName());

        Marker updatedMarker = markerRepository.save(marker);

        return Optional.of(toResponse(updatedMarker));
    }

    // Удаление маркера по идентификатору.
    // Repository удаляет запись из хранилища и возвращает результат операции.
    public boolean deleteById(Long id) {
        return markerRepository.deleteById(id);
    }

    // Преобразование модели Marker в DTO ответа.
    // В DTO передаются идентификатор и название маркера.
    // Метод используется при получении, создании и обновлении маркеров.
    private MarkerResponseTo toResponse(Marker marker) {
        return new MarkerResponseTo(
                marker.getId(),
                marker.getName()
        );
    }
}
