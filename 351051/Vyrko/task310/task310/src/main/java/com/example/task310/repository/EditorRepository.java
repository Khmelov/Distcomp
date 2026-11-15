
package com.example.task310.repository;

import com.example.task310.model.Editor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Репозиторий отвечает за доступ к данным редакторов.
// Реализует общий интерфейс InMemoryRepository для модели Editor.
// Используется EditorService для выполнения операций с редакторами.
@Repository
public class EditorRepository implements InMemoryRepository<Editor> {

    // Универсальное хранилище редакторов.
    // Editor::getId и Editor::setId передают методы получения
    // и установки идентификатора модели Editor.
    private final InMemoryRepositoryImpl<Editor> repository =
            new InMemoryRepositoryImpl<>(Editor::getId, Editor::setId);

    // Получение списка всех редакторов из хранилища.
    @Override
    public List<Editor> findAll() {
        return repository.findAll();
    }

    // Поиск редактора по идентификатору.
    // Возвращает Optional с найденным объектом либо Optional.empty().
    @Override
    public Optional<Editor> findById(Long id) {
        return repository.findById(id);
    }

    // Поиск редактора по логину.
    // Получает список редакторов и находит первый объект,
    // у которого логин совпадает с переданным значением.
    // Используется EditorService при проверке уникальности логина.
    public Optional<Editor> findByLogin(String login) {
        return repository.findAll()
                .stream()
                .filter(editor -> editor.getLogin().equals(login))
                .findFirst();
    }

    // Сохранение нового или обновлённого редактора.
    // Передаёт объект универсальному хранилищу.
    @Override
    public Editor save(Editor editor) {
        return repository.save(editor);
    }

    // Удаление редактора по идентификатору.
    @Override
    public boolean deleteById(Long id) {
        return repository.deleteById(id);
    }

    // Проверка существования редактора по идентификатору.
    @Override
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }
}
