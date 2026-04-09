<?php
namespace App\Exception;

class ApiException extends \Exception {
    private int $apiCode;

    public function __construct(int $httpStatus, int $subCode, string $message) {
        parent::__construct($message, $httpStatus);
        $this->apiCode = ($httpStatus * 100) + $subCode;
    }

    public function getApiCode(): int { return $this->apiCode; }
}