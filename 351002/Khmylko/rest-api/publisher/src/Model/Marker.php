<?php
namespace App\Model;

class Marker {
    private ?int $id = null;
    private string $name;
    private string $created;
    private string $modified;

    public function __construct() {
        $this->created = date('Y-m-d H:i:s');
        $this->modified = date('Y-m-d H:i:s');
    }

    public function getId(): ?int { return $this->id; }
    public function setId(int $id): self { $this->id = $id; return $this; }
    public function getName(): string { return $this->name; }
    public function setName(string $name): self { $this->name = $name; return $this; }

    public function toArray(): array {
        return [
            'id' => $this->id,
            'name' => $this->name
        ];
    }
}