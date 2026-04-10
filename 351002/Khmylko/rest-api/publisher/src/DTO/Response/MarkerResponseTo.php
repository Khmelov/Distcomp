<?php
namespace App\DTO\Response;

class MarkerResponseTo {
    private array $data;

    public function __construct(array $data) {
        $this->data = $data;
    }

    public function toArray(): array {
        return ['marker' => $this->data];
    }
}