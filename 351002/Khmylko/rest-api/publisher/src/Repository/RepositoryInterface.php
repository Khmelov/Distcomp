<?php
namespace App\Repository;

interface RepositoryInterface {
    public function findById(int $id): ?array;
    public function findAll(): array;
    public function delete(int $id): bool;
    public function exists(int $id): bool;
}