<?php
namespace App\Service;

use App\Repository\MarkerRepository;
use App\Exception\ValidationException;
use App\Exception\NotFoundException;

class MarkerService {
    private MarkerRepository $repository;

    public function __construct(MarkerRepository $repository) {
        $this->repository = $repository;
    }

    public function findById(int $id): array {
        $marker = $this->repository->findById($id);
        if (!$marker) {
            throw new NotFoundException('Marker', $id);
        }
        return $this->map($marker);
    }

    public function findAll(int $page = 1, int $limit = 10, string $sortBy = 'id', string $order = 'ASC'): array {
        $markers = $this->repository->findAll([], $sortBy, $order, $page, $limit);
        return array_map([$this, 'map'], $markers);
    }

    public function create(array $data): array {
        if (!isset($data['name']) || trim($data['name']) === '') {
            throw new ValidationException("Marker name required");
        }

        if (strlen($data['name']) < 2 || strlen($data['name']) > 32) {
            throw new ValidationException("Marker name must be 2-32 characters");
        }

        $marker = $this->repository->create($data);
        return $this->map($marker);
    }

    public function update(int $id, array $data): array {
        $this->findById($id);

        if (isset($data['name'])) {
            $marker = $this->repository->update($id, $data);
            return $this->map($marker);
        }

        return $this->map($this->repository->findById($id));
    }

    public function delete(int $id): void {
        $this->findById($id);
        $this->repository->delete($id);
    }
    public function findOrCreateByName(string $name): array {
        // Ищем существующий маркер
        $marker = $this->repository->findByName($name);
        if ($marker) {
            return $marker;
        }

        // Создаём новый
        return $this->create(['name' => $name]);
    }
    public function getById(int $id): array {
        return $this->findById($id);
    }

    private function map(array $marker): array {
        return [
            'id' => $marker['id'],
            'name' => $marker['name']
        ];
    }
}