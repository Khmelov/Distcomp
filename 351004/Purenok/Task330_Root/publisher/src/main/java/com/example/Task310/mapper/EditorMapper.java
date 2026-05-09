package com.example.Task310.mapper;

import com.example.Task310.bean.Editor;
import com.example.Task310.dto.EditorRequestTo;
import com.example.Task310.dto.EditorResponseTo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EditorMapper {
    EditorResponseTo toResponse(Editor editor);
    Editor toEntity(EditorRequestTo request);
<<<<<<< HEAD:351004/Purenok/Task330_Root/publisher/src/main/java/com/example/Task310/mapper/EditorMapper.java
    
    void updateEntity(EditorRequestTo request, @MappingTarget Editor editor);
}

=======
    void updateEntity(EditorRequestTo request, @MappingTarget Editor editor);
}
>>>>>>> e9b46436b12a679a1122bfd9ca7840c196ff410a:351004/Purenok/Task310/src/main/java/com/example/Task310/mapper/EditorMapper.java
