
package com.example.task310.repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.Function;

// Универсальная реализация интерфейса InMemoryRepository.
// Обеспечивает хранение объектов разных типов в оперативной памяти.
// Используется конкретными репозиториями для выполнения стандартных CRUD-операций.
public class InMemoryRepositoryImpl<T> implements InMemoryRepository<T> {

    // Основное хранилище объектов.
    // В качестве ключа используется идентификатор объекта,
    // а в качестве значения — сам объект.
    // LinkedHashMap сохраняет порядок добавления записей.
    private final Map<Long, T> storage = new LinkedHashMap<>();

    // Счётчик для автоматического назначения идентификаторов новым объектам.
    // Нумерация начинается с единицы.
    private final AtomicLong idCounter = new AtomicLong(1);

    // Функция получения идентификатора из объекта.
    // Конкретный репозиторий передаёт способ получения ID своей модели.
    private final Function<T, Long> idGetter;

    // Функция установки идентификатора объекту.
    // Позволяет присвоить новый ID без знания конкретного типа модели.
    private final BiConsumer<T, Long> idSetter;

    // Конструктор принимает функции получения и установки ID.
    // Благодаря этому одна реализация может работать с разными моделями.
    public InMemoryRepositoryImpl(
            Function<T, Long> idGetter,
            BiConsumer<T, Long> idSetter) {
        this.idGetter = idGetter;
        this.idSetter = idSetter;
    }

    // Получение всех объектов из хранилища.
    // values() возвращает коллекцию сохранённых объектов.
    // Создаётся новый список, содержащий найденные объекты.
    @Override
    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    // Поиск объекта по идентификатору.
    // storage.get(id) возвращает объект, если такой ключ существует.
    // ofNullable преобразует результат в Optional,
    // позволяя корректно обработать отсутствие записи.
    @Override
    public Optional<T> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    // Сохранение нового или обновлённого объекта.
    // Сначала с помощью idGetter определяется текущий идентификатор.
    @Override
    public T save(T entity) {
        Long id = idGetter.apply(entity);

        // Если идентификатор отсутствует, объект считается новым.
        // Счётчик выдаёт следующий свободный ID,
        // после чего idSetter записывает его непосредственно в объект.
        if (id == null) {
            id = idCounter.getAndIncrement();
            idSetter.accept(entity, id);
        } else {
            // Если ID уже существует, объект сохраняется под этим идентификатором.
            // Счётчик обновляется так, чтобы следующий автоматически выданный ID
            // был больше уже существующего идентификатора.
            Long savedId = id;
            idCounter.updateAndGet(current -> Math.max(current, savedId + 1));
        }

        // Добавление объекта в хранилище или замена существующей записи.
        storage.put(id, entity);

        // Возвращается сохранённый объект с его идентификатором.
        return entity;
    }

    // Удаление объекта по идентификатору.
    // Метод remove() удаляет запись и возвращает её значение.
    // Если записи не было, возвращается null.
    @Override
    public boolean deleteById(Long id) {
        return storage.remove(id) != null;
    }

    // Проверка наличия объекта в хранилище.
    // containsKey() проверяет, существует ли запись с указанным ID.
    @Override
    public boolean existsById(Long id) {
        return storage.containsKey(id);
    }
}
