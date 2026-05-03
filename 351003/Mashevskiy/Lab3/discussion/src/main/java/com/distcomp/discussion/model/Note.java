package com.distcomp.discussion.model;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Table("tbl_note")
public class Note {

    @PrimaryKey
    private NoteKey id;

    @NotBlank
    @Size(min = 2, max = 2048)
    private String content;

    public Note() {}

    public Note(NoteKey id, String content) {
        this.id = id;
        this.content = content;
    }

    public NoteKey getId() { return id; }
    public void setId(NoteKey id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}