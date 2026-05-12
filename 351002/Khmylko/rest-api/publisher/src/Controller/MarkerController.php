<?php
namespace App\Controller;

use App\Service\MarkerService;

class MarkerController {
    private MarkerService $service;

    public function __construct(MarkerService $service) {
        $this->service = $service;
    }

    public function getAll(): array {
        return $this->service->getAll();
    }

    public function getById(int $id): array {
        return $this->service->getById($id);
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