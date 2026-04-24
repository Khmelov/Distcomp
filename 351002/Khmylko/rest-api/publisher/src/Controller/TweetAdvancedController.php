<?php
declare(strict_types=1);

namespace App\Controller;

use App\Service\TweetService;
use App\Service\EditorService;
use App\Service\MarkerService;
use App\Service\NoticeService;
use Psr\Http\Message\ResponseInterface as Response;
use Psr\Http\Message\ServerRequestInterface as Request;

class TweetAdvancedController
{
    private TweetService $tweetService;
    private EditorService $editorService;
    private MarkerService $markerService;
    private NoticeService $noticeService;

    public function __construct(
        TweetService $tweetService,
        EditorService $editorService,
        MarkerService $markerService,
        NoticeService $noticeService
    ) {
        $this->tweetService = $tweetService;
        $this->editorService = $editorService;
        $this->markerService = $markerService;
        $this->noticeService = $noticeService;
    }

    public function getEditorByTweetId(Request $request, Response $response, array $args): Response
    {
        $tweetId = (int)$args['tweetId'];
        $tweet = $this->tweetService->findById($tweetId);
        $editor = $this->editorService->findById($tweet->toArray()['tweet']['editorId']);

        $response->getBody()->write(json_encode($editor->toArray()));
        return $response->withHeader('Content-Type', 'application/json')
            ->withStatus(200);
    }

    public function getMarkersByTweetId(Request $request, Response $response, array $args): Response
    {
        $tweetId = (int)$args['tweetId'];
        $tweet = $this->tweetService->findById($tweetId);

        // Здесь должна быть логика получения маркеров по tweetId
        // Для примера возвращаем все маркеры
        $markers = $this->markerService->findAll();
        $data = array_map(fn($marker) => $marker->toArray(), $markers);

        $response->getBody()->write(json_encode($data));
        return $response->withHeader('Content-Type', 'application/json')
            ->withStatus(200);
    }

    public function getNoticesByTweetId(Request $request, Response $response, array $args): Response
    {
        $tweetId = (int)$args['tweetId'];
        $tweet = $this->tweetService->findById($tweetId);

        // Здесь должна быть логика получения уведомлений по tweetId
        // Для примера возвращаем все уведомления
        $notices = $this->noticeService->findAll();
        $data = array_map(fn($notice) => $notice->toArray(), $notices);

        $response->getBody()->write(json_encode($data));
        return $response->withHeader('Content-Type', 'application/json')
            ->withStatus(200);
    }

    public function searchTweets(Request $request, Response $response): Response
    {
        $params = $request->getQueryParams();

        // Здесь должна быть сложная логика поиска
        // Пока возвращаем все твиты
        $tweets = $this->tweetService->findAll();
        $data = array_map(fn($tweet) => $tweet->toArray(), $tweets);

        $response->getBody()->write(json_encode($data));
        return $response->withHeader('Content-Type', 'application/json')
            ->withStatus(200);
    }

    public function exists(int $id): bool {
        return $this->service->exists($id);
    }
}