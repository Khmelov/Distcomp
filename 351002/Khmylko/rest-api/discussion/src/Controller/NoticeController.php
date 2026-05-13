<?php
namespace Discussion\Controller;

use Discussion\Service\NoticeService;
use Discussion\Exception\ApiException;

class NoticeController {
    private NoticeService $service;

    public function __construct() {
        $this->service = new NoticeService();
    }

    public function handleRequest(): void {
        $method = $_SERVER['REQUEST_METHOD'];
        $uri = $_SERVER['REQUEST_URI'];
        $parts = explode('/', trim($uri, '/'));

        // /api/v1.0/notices[/id]
        $id = $parts[3] ?? null;

        try {
            switch ($method) {
                case 'GET':
                    if ($id) {
                        $result = $this->service->getById($id);
                        if (!$result) {
                            throw new ApiException(404, 40401, "Notice not found");
                        }
                        echo json_encode($result);
                    } else {
                        echo json_encode($this->service->findAll());
                    }
                    break;

                case 'POST':
                    $data = json_decode(file_get_contents('php://input'), true) ?? [];
                    $result = $this->service->create($data);
                    http_response_code(201);
                    echo json_encode($result);
                    break;

                case 'PUT':
                    if (!$id) {
                        throw new ApiException(400, 40001, "ID required");
                    }
                    $data = json_decode(file_get_contents('php://input'), true) ?? [];
                    $result = $this->service->update($id, $data);
                    echo json_encode($result);
                    break;

                case 'DELETE':
                    if (!$id) {
                        throw new ApiException(400, 40001, "ID required");
                    }
                    $this->service->delete($id);
                    http_response_code(204);
                    break;

                default:
                    throw new ApiException(405, 40501, "Method not allowed");
            }
        } catch (ApiException $e) {
            http_response_code($e->getCode());
            echo json_encode([
                "errorMessage" => $e->getMessage(),
                "errorCode" => $e->getApiCode()
            ]);
        } catch (\Exception $e) {
            http_response_code(500);
            echo json_encode([
                "errorMessage" => "Internal Error: " . $e->getMessage(),
                "errorCode" => 50000
            ]);
        }
    }
}