
package com.example.task310.service;

import com.example.task310.dto.EditorRequestTo;
import com.example.task310.dto.EditorResponseTo;
import com.example.task310.exception.DuplicateLoginException;
import com.example.task310.model.Editor;
import com.example.task310.repository.EditorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// Сервисный слой содержит бизнес-логику работы с редакторами.
// Получает запросы от EditorController и взаимодействует с EditorRepository.
// Также выполняет преобразование моделей Editor в DTO и обратно.
@Service
public class EditorService {

    // Репозиторий используется для хранения, поиска и удаления редакторов.
    private final EditorRepository editorRepository;

    // Spring передаёт репозиторий через конструктор (внедрение зависимости).
    public EditorService(EditorRepository editorRepository) {
        this.editorRepository = editorRepository;
    }

    // Получение списка всех редакторов.
    // EditorRepository возвращает список моделей Editor.
    // Stream преобразует каждую модель в EditorResponseTo с помощью toResponse().
    // В результате контроллер получает список DTO без паролей редакторов.
    public List<EditorResponseTo> findAll() {
        return editorRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Поиск редактора по идентификатору.
    // Repository возвращает Optional с моделью Editor,
    // если запись найдена.
    // Метод map преобразует найденную модель в EditorResponseTo.
    // Если редактор отсутствует, возвращается Optional.empty().
    public Optional<EditorResponseTo> findById(Long id) {
        return editorRepository.findById(id)
                .map(this::toResponse);
    }

    // Создание нового редактора.
    // На вход поступает EditorRequestTo с данными от клиента.
    // Сначала проверяется, не занят ли указанный логин другим редактором.
    // Затем создаётся модель Editor и заполняются её поля данными из DTO.
    // EditorRepository сохраняет модель и назначает ей идентификатор.
    // Сохранённая модель преобразуется в EditorResponseTo и возвращается контроллеру.
    public EditorResponseTo save(EditorRequestTo request) {
        checkLogin(request.getLogin(), null);

        Editor editor = new Editor();
        editor.setLogin(request.getLogin());
        editor.setPassword(request.getPassword());
        editor.setFirstname(request.getFirstname());
        editor.setLastname(request.getLastname());

        Editor savedEditor = editorRepository.save(editor);

        return toResponse(savedEditor);
    }

    // Обновление существующего редактора.
    // Перед изменением проверяется уникальность логина.
    // Текущий идентификатор передаётся в checkLogin(),
    // чтобы редактор мог сохранить собственный логин.
    // Создаётся модель Editor с прежним идентификатором и новыми данными.
    // Repository сохраняет обновлённую модель.
    // Результат преобразуется в EditorResponseTo.
    public EditorResponseTo update(Long id, EditorRequestTo request) {
        checkLogin(request.getLogin(), id);

        Editor editor = new Editor();
        editor.setId(id);
        editor.setLogin(request.getLogin());
        editor.setPassword(request.getPassword());
        editor.setFirstname(request.getFirstname());
        editor.setLastname(request.getLastname());

        Editor updatedEditor = editorRepository.save(editor);

        return toResponse(updatedEditor);
    }

    // Проверка уникальности логина редактора.
    // EditorRepository ищет запись с указанным логином.
    // Если логин уже используется другим редактором,
    // выбрасывается DuplicateLoginException.
    // При создании currentEditorId равен null,
    // при обновлении содержит идентификатор текущего редактора.
    private void checkLogin(String login, Long currentEditorId) {
        Optional<Editor> existingEditor = editorRepository.findByLogin(login);

        if (existingEditor.isPresent()
                && !existingEditor.get().getId().equals(currentEditorId)) {
            throw new DuplicateLoginException(
                    "Editor with login '" + login + "' already exists"
            );
        }
    }

    // Удаление редактора по идентификатору.
    // Repository выполняет удаление из внутреннего хранилища.
    // Возвращает true, если запись была удалена, иначе false.
    public boolean deleteById(Long id) {
        return editorRepository.deleteById(id);
    }

    // Преобразование модели Editor в DTO EditorResponseTo.
    // В ответ включаются идентификатор, логин, имя и фамилия.
    // Пароль намеренно не передаётся клиенту.
    // Этот метод используется при получении, создании и обновлении редактора.
    private EditorResponseTo toResponse(Editor editor) {
        return new EditorResponseTo(
                editor.getId(),
                editor.getLogin(),
                editor.getFirstname(),
                editor.getLastname()
        );
    }
}
