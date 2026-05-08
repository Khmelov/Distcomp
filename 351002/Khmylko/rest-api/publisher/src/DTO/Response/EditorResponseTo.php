<?php
namespace App\DTO\Response;

class EditorResponseTo {
    private array $data;

    public function __construct(array $data) {
        $this->data = $data;
    }

    public function toArray(): array {
        return ['editor' => $this->data];
    }
}