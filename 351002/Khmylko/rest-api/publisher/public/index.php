<?php
declare(strict_types=1);

header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');
header('Content-Type: application/json');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

require __DIR__ . '/../vendor/autoload.php';

use App\App\FrontController;
use App\Exception\ApiException;

try {
    FrontController::fromEnvironment()->run();
} catch (ApiException $e) {
    http_response_code($e->getCode());
    echo json_encode([
        'errorMessage' => $e->getMessage(),
        'errorCode' => $e->getApiCode(),
    ], JSON_UNESCAPED_UNICODE);
} catch (Throwable $e) {
    http_response_code(500);
    echo json_encode([
        'errorMessage' => 'Internal Error: ' . $e->getMessage(),
        'errorCode' => 50000,
    ], JSON_UNESCAPED_UNICODE);
}
