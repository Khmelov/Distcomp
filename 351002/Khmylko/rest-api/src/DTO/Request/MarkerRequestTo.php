<?php
namespace App\DTO\Request;

class MarkerRequestTo {
    private string $name;

    public function __construct(array $data) {
        $this->name = $data['name'] ?? '';
    }

    public function getName(): string { return $this->name; }
}