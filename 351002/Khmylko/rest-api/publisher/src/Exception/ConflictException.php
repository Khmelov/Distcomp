<?php
namespace App\Exception;

class ConflictException extends ApiException {
    public function __construct(string $message) {
        parent::__construct(403, 3, $message);  // 409 Conflict
    }
}