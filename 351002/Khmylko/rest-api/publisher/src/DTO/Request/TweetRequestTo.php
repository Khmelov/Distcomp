<?php
namespace App\DTO\Request;

class TweetRequestTo {
    private int $editorId;
    private string $title;
    private string $content;

    public function __construct(array $data) {
        $this->editorId = $data['editorId'] ?? 0;
        $this->title = $data['title'] ?? '';
        $this->content = $data['content'] ?? '';
    }

    public function getEditorId(): int { return $this->editorId; }
    public function getTitle(): string { return $this->title; }
    public function getContent(): string { return $this->content; }
}