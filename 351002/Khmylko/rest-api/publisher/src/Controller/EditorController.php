<?php
namespace App\Controller;

use App\Service\EditorService;

class EditorController {
    private EditorService $service;

    public function __construct(EditorService $service) {
        $this->service = $service;
    }

    public function getAll(): array {
        return $this->service->getAll();
    }

    public function getById(int $id): array {
        $result = $this->service->getById($id);
        if (!$result) {
            return ['error' => 'Editor not found'];
        }
        return $result;
    }

    public function create(array $data): array {
        return $this->service->create($data);
    }

    public function update(int $id, array $data): array {
        return $this->service->update($id, $data);
    }

    public function delete(int $id): void {
        $this->service->delete($id);
    }
}