<?php
namespace App\Model;

class Notice {
    private ?int $id = null;
    private int $tweetId;
    private string $content;
    private string $created;
    private string $modified;

    public function __construct() {
        $this->created = date('Y-m-d H:i:s');
        $this->modified = date('Y-m-d H:i:s');
    }

    public function getId(): ?int { return $this->id; }
    public function setId(int $id): self { $this->id = $id; return $this; }
    public function getTweetId(): int { return $this->tweetId; }
    public function setTweetId(int $tweetId): self { $this->tweetId = $tweetId; return $this; }
    public function getContent(): string { return $this->content; }
    public function setContent(string $content): self { $this->content = $content; return $this; }

    public function toArray(): array {
        return [
            'id' => $this->id,
            'tweetId' => $this->tweetId,
            'content' => $this->content
        ];
    }
}