<?php
// ========== CORS ЗАГОЛОВКИ ==========
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');
header('Content-Type: application/json');

// Обработка preflight OPTIONS запроса
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

require __DIR__ . '/../vendor/autoload.php';

use App\Exception\ApiException;
use App\Repository\EditorRepository;
use App\Repository\TweetRepository;
use App\Repository\MarkerRepository;
use App\Repository\NoticeRepository;
use App\Service\EditorService;
use App\Service\TweetService;
use App\Service\MarkerService;
use App\Service\NoticeService;

try {
    // Инициализация репозиториев
    $editorRepo = new EditorRepository();
    $tweetRepo = new TweetRepository();
    $markerRepo = new MarkerRepository();
    $noticeRepo = new NoticeRepository();

    $services = [
        'editors' => new EditorService($editorRepo),
        'tweets'  => new TweetService($tweetRepo, $editorRepo, new MarkerService($markerRepo)),
        'markers' => new MarkerService($markerRepo),
        'notices' => new NoticeService($noticeRepo, $tweetRepo)
    ];

    $uri = parse_url($_SERVER['REQUEST_URI'], PHP_URL_PATH);
    $parts = explode('/', trim($uri, '/'));

    // Проверка префикса api/v1.0
    if (count($parts) < 3 || $parts[0] !== 'api' || $parts[1] !== 'v1.0') {
        throw new ApiException(404, 0, "Endpoint not found");
    }

    $resource = $parts[2];
    $id = isset($parts[3]) ? (int)$parts[3] : null;

    if (!isset($services[$resource])) {
        throw new ApiException(404, 99, "Unknown resource");
    }

    $service = $services[$resource];
    $method = $_SERVER['REQUEST_METHOD'];
    $out = null;

    switch ($method) {
        case 'GET':
            if ($id) {
                $out = $service->getById($id);
            } else {
                $page = (int)($_GET['page'] ?? 1);
                $limit = (int)($_GET['limit'] ?? 10);
                $sort = $_GET['sort'] ?? 'id';
                $order = $_GET['order'] ?? 'ASC';
                $out = $service->findAll($page, $limit, $sort, $order);
            }
            break;

        case 'POST':
            $data = json_decode(file_get_contents('php://input'), true) ?? [];
            $out = $service->create($data);
            http_response_code(201);
            break;

        case 'PUT':
            if (!$id) throw new ApiException(400, 5, "ID required");
            $data = json_decode(file_get_contents('php://input'), true) ?? [];
            $out = $service->update($id, $data);
            break;

        case 'DELETE':
            if (!$id) throw new ApiException(400, 6, "ID required");
            $service->delete($id);
            http_response_code(204);
            exit;

        default:
            throw new ApiException(405, 0, "Method not allowed");
    }

    echo json_encode($out, JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT);

} catch (ApiException $e) {
    http_response_code($e->getCode());
    echo json_encode([
        "errorMessage" => $e->getMessage(),
        "errorCode"    => $e->getApiCode()
    ], JSON_UNESCAPED_UNICODE);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        "errorMessage" => "Internal Error: " . $e->getMessage(),
        "errorCode"    => 50000
    ]);
}