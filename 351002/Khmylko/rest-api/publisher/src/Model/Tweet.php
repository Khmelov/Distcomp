<?php
namespace App\Model;

class Tweet {
    private ?int $id = null;
    private int $editorId;
    private string $title;
    private string $content;
    private string $created;
    private string $modified;

    public function __construct() {
        $this->created = date('Y-m-d H:i:s');
        $this->modified = date('Y-m-d H:i:s');
    }

    public function getId(): ?int { return $this->id; }
    public function setId(int $id): self { $this->id = $id; return $this; }
    public function getEditorId(): int { return $this->editorId; }
    public function setEditorId(int $editorId): self { $this->editorId = $editorId; return $this; }
    public function getTitle(): string { return $this->title; }
    public function setTitle(string $title): self { $this->title = $title; return $this; }
    public function getContent(): string { return $this->content; }
    public function setContent(string $content): self { $this->content = $content; return $this; }

    public function toArray(): array {
        return [
            'id' => $this->id,
            'editorId' => $this->editorId,
            'title' => $this->title,
            'content' => $this->content
        ];
    }
}