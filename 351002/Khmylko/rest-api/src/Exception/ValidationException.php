<?php
namespace App\Exception;

class ValidationException extends ApiException {
    public function __construct(string $message) {
        parent::__construct(400, 2, $message);
    }
}