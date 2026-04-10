<?php
namespace App\Service;

use App\Exception\ConflictException;
use App\Repository\TweetRepository;
use App\Repository\EditorRepository;
use App\Exception\ApiException;
use App\Exception\ValidationException;
use App\Exception\NotFoundException;

class TweetService {
    private TweetRepository $repository;
    private EditorRepository $editorRepository;
    private MarkerService $markerService;
    public function __construct(TweetRepository $repository, EditorRepository $editorRepo, MarkerService $markerService) {
        $this->repository = $repository;
        $this->editorRepository = $editorRepo;
        $this->markerService = $markerService;
    }

    public function getById(int $id): array {
        $tweet = $this->repository->findById($id);
        if (!$tweet) {
            throw new NotFoundException('Tweet', $id);
        }

        // Конвертируем editor_id в editorId
        if (isset($tweet['editor_id'])) {
            $tweet['editorId'] = $tweet['editor_id'];
            unset($tweet['editor_id']);
        }

        return $tweet;
    }

    public function findAll(int $page = 1, int $limit = 10, string $sortBy = 'id', string $order = 'ASC'): array {
        $tweets = $this->repository->findAll([], $sortBy, $order, $page, $limit);

        // Конвертируем editor_id в editorId для каждого твита
        foreach ($tweets as &$tweet) {
            if (isset($tweet['editor_id'])) {
                $tweet['editorId'] = $tweet['editor_id'];
                unset($tweet['editor_id']);
            }
        }

        return $tweets;
    }

    public function create(array $data): array {
        // Конвертируем editorId в editor_id
        if (isset($data['editorId'])) {
            $data['editor_id'] = $data['editorId'];
        }

        // Валидация
        if (empty($data['title']) || strlen($data['title']) < 2 || strlen($data['title']) > 64) {
            throw new ValidationException("Title must be 2-64 characters");
        }
        if (empty($data['content']) || strlen($data['content']) < 4 || strlen($data['content']) > 2048) {
            throw new ValidationException("Content must be 4-2048 characters");
        }
        if (empty($data['editor_id'])) {
            throw new ValidationException("editorId is required");
        }

        // Проверка существования редактора
        $editor = $this->editorRepository->findById($data['editor_id']);
        if (!$editor) {
            throw new ConflictException("Editor with id {$data['editor_id']} does not exist");
        }

        // Проверка уникальности title для этого редактора
        $existingTweet = $this->repository->findByEditorIdAndTitle($data['editor_id'], $data['title']);
        if ($existingTweet) {
            throw new ConflictException("Tweet with title '{$data['title']}' already exists for this editor");
        }

        // Создаём твит
        $tweet = $this->repository->create($data);
        $tweetId = $tweet['id'] ?? $tweet['tweet_id'] ?? null;

        if (!$tweetId) {
            throw new \Exception("Failed to create tweet");
        }

        // Привязываем маркеры, если они есть
        if (!empty($data['markers']) && is_array($data['markers'])) {
            foreach ($data['markers'] as $markerName) {
                // Ищем или создаём маркер
                $marker = $this->markerService->findOrCreateByName($markerName);

                // Привязываем маркер к твиту
                $this->repository->attachMarker($tweetId, $marker['id']);
            }
        }

        // Конвертируем обратно editor_id в editorId для ответа
        if (isset($tweet['editor_id'])) {
            $tweet['editorId'] = $tweet['editor_id'];
            unset($tweet['editor_id']);
        }

        return $tweet;
    }

    public function update(int $id, array $data): array {
        // Конвертируем editorId в editor_id
        if (isset($data['editorId'])) {
            $data['editor_id'] = $data['editorId'];
        }

        $existing = $this->repository->findById($id);
        if (!$existing) {
            throw new NotFoundException('Tweet', $id);
        }

        if (isset($data['title'])) $existing['title'] = $data['title'];
        if (isset($data['content'])) $existing['content'] = $data['content'];
        if (isset($data['editor_id'])) $existing['editor_id'] = $data['editor_id'];

        $tweet = $this->repository->update($id, $existing);

        // Конвертируем обратно editor_id в editorId для ответа
        if (isset($tweet['editor_id'])) {
            $tweet['editorId'] = $tweet['editor_id'];
            unset($tweet['editor_id']);
        }

        return $tweet;
    }

    public function delete(int $id): void {
        if (!$this->repository->exists($id)) {
            throw new NotFoundException('Tweet', $id);
        }
        $this->repository->delete($id);
    }

    private function validate(array $data): void {
        if (empty($data['title']) || strlen($data['title']) < 2 || strlen($data['title']) > 64) {
            throw new ValidationException("Title must be 2-64 characters");
        }
        if (empty($data['content']) || strlen($data['content']) < 4 || strlen($data['content']) > 2048) {
            throw new ValidationException("Content must be 4-2048 characters");
        }
        if (empty($data['editor_id'])) {
            throw new ValidationException("editorId is required");
        }
    }
}