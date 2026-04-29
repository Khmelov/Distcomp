package by.bsuir.task310.entity;

import java.util.ArrayList;
import java.util.List;

public class Story {
    private Long id;
    private Long editorId;
    private String title;
    private String content;
    private String created;
    private String modified;
    private List<Long> tagIds = new ArrayList<>();

    public Story() {
    }

    public Story(Long id, Long editorId, String title, String content, String created, String modified, List<Long> tagIds) {
        this.id = id;
        this.editorId = editorId;
        this.title = title;
        this.content = content;
        this.created = created;
        this.modified = modified;
        setTagIds(tagIds);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEditorId() {
        return editorId;
    }

    public void setEditorId(Long editorId) {
        this.editorId = editorId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public List<Long> getTagIds() {
        return new ArrayList<>(tagIds);
    }

    public void setTagIds(List<Long> tagIds) {
        this.tagIds = tagIds == null ? new ArrayList<>() : new ArrayList<>(tagIds);
    }
}
