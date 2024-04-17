<?php

namespace App\CRUD;

use App\Entity\Model;

interface CRUDRepositoryInterface
{
    public function create(object $entity): void;
    
    public function update(object $entity): void;
    
    public function delete(object $entity): void;
    
    public function findById(int $id): ?object;
    
    public function findAll(): array;
}
