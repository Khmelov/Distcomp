package by.bsuir.task310.mapper;

import by.bsuir.task310.dto.request.EditorRequestTo;
import by.bsuir.task310.dto.response.EditorResponseTo;
import by.bsuir.task310.entity.Editor;

public final class EditorMapper {
    private EditorMapper() {
    }

    public static Editor toEntity(EditorRequestTo request) {
        return new Editor(
                request.id(),
                request.login(),
                request.password(),
                request.firstname(),
                request.lastname()
        );
    }

    public static EditorResponseTo toResponse(Editor editor) {
        return new EditorResponseTo(
                editor.getId(),
                editor.getLogin(),
                editor.getPassword(),
                editor.getFirstname(),
                editor.getLastname()
        );
    }
}
