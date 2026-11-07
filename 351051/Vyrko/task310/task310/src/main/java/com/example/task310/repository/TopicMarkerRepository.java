
package com.example.task310.repository;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// Репозиторий отвечает за хранение связей между темами и маркерами.
// Реализует отношение "многие ко многим".
// Хранит идентификаторы связанных объектов, а не сами модели Topic и Marker.
@Repository
public class TopicMarkerRepository {

    // Структура хранения связей:
    // ключ — идентификатор темы;
    // значение — множество идентификаторов маркеров этой темы.
    // HashSet исключает повторное добавление одного маркера к теме.
    private final Map<Long, Set<Long>> topicMarkers = new HashMap<>();

    // Добавление связи между темой и маркером.
    // computeIfAbsent() создаёт множество для темы, если его ещё нет.
    // После этого идентификатор маркера добавляется в множество.
    public void addMarker(Long topicId, Long markerId) {
        topicMarkers
                .computeIfAbsent(topicId, id -> new HashSet<>())
                .add(markerId);
    }

    // Удаление связи между конкретной темой и маркером.
    // Сначала находится множество маркеров указанной темы.
    // Если оно существует, из него удаляется нужный маркер.
    // Если после удаления множество стало пустым,
    // запись темы полностью удаляется из хранилища связей.
    public void removeMarker(Long topicId, Long markerId) {
        Set<Long> markers = topicMarkers.get(topicId);

        if (markers != null) {
            markers.remove(markerId);

            if (markers.isEmpty()) {
                topicMarkers.remove(topicId);
            }
        }
    }

    // Получение идентификаторов всех маркеров конкретной темы.
    // Если тема отсутствует в хранилище, возвращается пустое множество.
    // Создание нового HashSet не позволяет изменять внутреннее хранилище
    // напрямую через полученный результат.
    public Set<Long> findMarkerIdsByTopicId(Long topicId) {
        return new HashSet<>(
                topicMarkers.getOrDefault(topicId, Set.of())
        );
    }

    // Получение идентификаторов всех тем, связанных с указанным маркером.
    // Перебираются все записи хранилища связей.
    // Если множество маркеров темы содержит нужный ID,
    // идентификатор этой темы добавляется в результат.
    public Set<Long> findTopicIdsByMarkerId(Long markerId) {
        Set<Long> result = new HashSet<>();

        topicMarkers.forEach((topicId, markerIds) -> {
            if (markerIds.contains(markerId)) {
                result.add(topicId);
            }
        });

        return result;
    }

    // Удаление всех связей указанной темы с маркерами.
    // Сами маркеры при этом не удаляются.
    public void deleteByTopicId(Long topicId) {
        topicMarkers.remove(topicId);
    }

    // Удаление указанного маркера из всех тем.
    // После удаления проверяются множества связей:
    // если у темы больше не осталось маркеров, её запись удаляется.
    // Сами темы при этом остаются в хранилище.
    public void deleteByMarkerId(Long markerId) {
        topicMarkers.values().forEach(markers -> markers.remove(markerId));
        topicMarkers.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }
}
