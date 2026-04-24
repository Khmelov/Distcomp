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

    // Добавь этот метод
    public function getAll(): array {
        return $this->repository->findAll();
    }

    public function getById(int $id): array {
        $marker = $this->repository->findById($id);
        if (!$marker) {
            throw new NotFoundException('Marker', $id);
        }
        return $marker;
    }

    public function create(array $data): array {
        if (empty($data['name']) || strlen($data['name']) < 2 || strlen($data['name']) > 32) {
            throw new ValidationException("Name must be 2-32 characters");
        }
        return $this->repository->create($data);
    }

    public function update(int $id, array $data): array {
        $this->getById($id);
        return $this->repository->update($id, $data);
    }

    public function delete(int $id): void {
        $this->getById($id);
        $this->repository->delete($id);
    }

    public function findOrCreateByName(string $name): array {
        $marker = $this->repository->findByName($name);
        if (!$marker) {
            $marker = $this->create(['name' => $name]);
        }
        return $marker;
    }
}