<?php
namespace App\DTO\Request;

class NoticeRequestTo {
    private int $tweetId;
    private string $content;

    public function __construct(array $data) {
        $this->tweetId = $data['tweetId'] ?? 0;
        $this->content = $data['content'] ?? '';
    }

    public function getTweetId(): int { return $this->tweetId; }
    public function getContent(): string { return $this->content; }
}