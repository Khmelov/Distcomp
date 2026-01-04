package com.blog.mapper;

import com.blog.dto.request.EditorRequestTo;
import com.blog.dto.response.EditorResponseTo;
import com.blog.model.Editor;
import org.springframework.stereotype.Component;

@Component
public class EditorMapper {

    public Editor toEntity(EditorRequestTo request) {
        if (request == null) {
            return null;
        }

        Editor editor = new Editor();
        editor.setLogin(request.getLogin());
        editor.setPassword(request.getPassword());
        editor.setFirstname(request.getFirstname());
        editor.setLastname(request.getLastname());
        return editor;
    }

    public EditorResponseTo toResponse(Editor entity) {
        if (entity == null) {
            return null;
        }

        EditorResponseTo response = new EditorResponseTo();
        response.setId(entity.getId());
        response.setLogin(entity.getLogin());
        response.setFirstname(entity.getFirstname());
        response.setLastname(entity.getLastname());
        return response;
    }
}