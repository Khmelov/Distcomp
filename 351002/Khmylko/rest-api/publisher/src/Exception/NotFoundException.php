<?php
namespace App\Exception;

class NotFoundException extends ApiException {
    public function __construct(string $entity, int $id) {
        parent::__construct(404, 1, "$entity with id $id not found");
    }
}