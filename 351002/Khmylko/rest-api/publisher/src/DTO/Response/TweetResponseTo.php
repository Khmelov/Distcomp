<?php
namespace App\DTO\Response;

class TweetResponseTo {
    private array $data;

    public function __construct(array $data) {
        $this->data = $data;
    }

    public function toArray(): array {
        return ['tweet' => $this->data];
    }
}