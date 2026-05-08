<?php
namespace App\Exception;

use Exception;

class ApiException extends Exception {
    private int $apiCode;

    public function __construct(int $httpCode, int $apiCode, string $message) {
        parent::__construct($message, $httpCode);
        $this->apiCode = (int)($httpCode . str_pad($apiCode, 2, '0', STR_PAD_LEFT));
    }

    public function getApiCode(): int {
        return $this->apiCode;
    }
}