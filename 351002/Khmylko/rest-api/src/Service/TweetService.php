<?php
namespace App\Service;

use App\Repository\TweetRepository;
use App\Repository\EditorRepository;
use App\Exception\ApiException;
use App\Exception\ValidationException;
use App\Exception\NotFoundException;

class TweetService {
    private TweetRepository $repository;
    private EditorRepository $editorRepository;

    public function __construct(TweetRepository $repository, EditorRepository $editorRepo) {
        $this->repository = $repository;
        $this->editorRepository = $editorRepo;
    }

    public function getById(int $id): array {
        $tweet = $this->repository->findById($id);
        if (!$tweet) {
            throw new NotFoundException('Tweet', $id);
        }
        return $tweet;
    }

    public function findAll(int $page = 1, int $limit = 10, string $sortBy = 'id', string $order = 'ASC'): array {
        return $this->repository->findAll([], $sortBy, $order, $page, $limit);
    }

    public function create(array $data): array {
        // Конвертируем editorId в editor_id
        if (isset($data['editorId'])) {
            $data['editor_id'] = $data['editorId'];
        }

        $this->validate($data);

        // Проверка существования редактора
        if (!$this->editorRepository->findById($data['editor_id'])) {
            throw new ValidationException("Editor with id {$data['editor_id']} does not exist");
        }

        $tweet = $this->repository->create($data);
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