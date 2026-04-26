<?php
namespace App\DTO\Response;

class NoticeResponseTo {
    private array $data;

    public function __construct(array $data) {
        $this->data = $data;
    }

    public function toArray(): array {
        return ['notice' => $this->data];
    }
}