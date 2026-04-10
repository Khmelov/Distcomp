<?php
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');
header('Content-Type: application/json');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

require __DIR__ . '/../vendor/autoload.php';

use App\Exception\ApiException;
use App\Repository\EditorRepository;
use App\Controller\EditorController;
use App\Controller\TweetController;
use App\Controller\MarkerController;
use App\Controller\NoticeController;
use App\Repository\TweetRepository;
use App\Repository\MarkerRepository;
use App\Service\EditorService;
use App\Service\TweetService;
use App\Service\MarkerService;
use App\Service\NoticeService;


try {
    $editorRepo = new EditorRepository();
    $tweetRepo = new TweetRepository();
    $markerRepo = new MarkerRepository();
    $noticeService = new NoticeService($tweetRepo);


    $controllers = [
        'editors' => new EditorController(new EditorService($editorRepo)),
        'tweets' => new TweetController(new TweetService($tweetRepo, $editorRepo,new MarkerService($markerRepo))),
        'markers' => new MarkerController(new MarkerService($markerRepo)),
        'notices' => new NoticeController($noticeService)
    ];

    $uri = parse_url($_SERVER['REQUEST_URI'], PHP_URL_PATH);
    $parts = explode('/', trim($uri, '/'));

    if (count($parts) < 3 || $parts[0] !== 'api' || $parts[1] !== 'v1.0') {
        throw new ApiException(404, 40400, "Endpoint not found");
    }

    $resource = $parts[2];
    $id = isset($parts[3]) ? (int)$parts[3] : null;

    if (!isset($controllers[$resource])) {
        throw new ApiException(404, 40400, "Resource not found");
    }

    $controller = $controllers[$resource];
    $method = $_SERVER['REQUEST_METHOD'];
    $out = null;

    switch ($method) {
        case 'GET':
            $out = $id ? $controller->getById($id) : $controller->getAll();
            break;
        case 'POST':
            $data = json_decode(file_get_contents('php://input'), true) ?? [];
            $out = $controller->create($data);
            http_response_code(201);
            break;
        case 'PUT':
            if (!$id) throw new ApiException(400, 40001, "ID required");
            $data = json_decode(file_get_contents('php://input'), true) ?? [];
            $out = $controller->update($id, $data);
            break;
        case 'DELETE':
            if (!$id) throw new ApiException(400, 40001, "ID required");
            $controller->delete($id);
            http_response_code(204);
            exit;
        default:
            throw new ApiException(405, 40500, "Method not allowed");
    }

    echo json_encode($out, JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT);

} catch (ApiException $e) {
    http_response_code($e->getCode());
    echo json_encode([
        "errorMessage" => $e->getMessage(),
        "errorCode" => $e->getApiCode()
    ]);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        "errorMessage" => "Internal Error: " . $e->getMessage(),
        "errorCode" => 50000
    ]);
}