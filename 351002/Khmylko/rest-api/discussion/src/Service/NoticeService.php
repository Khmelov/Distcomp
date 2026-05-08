<?php
namespace Discussion\Service;

use Discussion\Repository\NoticeRepository;
use Discussion\Exception\ApiException;

class NoticeService {
    private NoticeRepository $repository;

    public function __construct() {
        $this->repository = new NoticeRepository();
    }

    public function getById(string $id): ?array {
        return $this->repository->findById($id);
    }

    public function findAll(): array {
        return $this->repository->findAll();
    }

    public function create(array $data): array {
        // Валидация content
        if (empty($data['content'])) {
            throw new ApiException(400, 40002, "Content is required");
        }
        if (strlen($data['content']) < 4 || strlen($data['content']) > 2048) {
            throw new ApiException(400, 40003, "Content must be 4-2048 characters");
        }

        // Валидация tweet_id
        if (empty($data['tweet_id'])) {
            throw new ApiException(400, 40004, "tweetId is required");
        }

        return $this->repository->create($data);
    }

    public function update(string $id, array $data): array {
        $existing = $this->repository->findById($id);
        if (!$existing) {
            throw new ApiException(404, 40401, "Notice not found");
        }

        if (isset($data['content'])) {
            if (strlen($data['content']) < 4 || strlen($data['content']) > 2048) {
                throw new ApiException(400, 40003, "Content must be 4-2048 characters");
            }
        }

        $payload = $data;
        unset($payload['state']);

        return $this->repository->update($id, $payload);
    }

    public function delete(string $id): void {
        $existing = $this->repository->findById($id);
        if (!$existing) {
            throw new ApiException(404, 40401, "Notice not found");
        }
        $this->repository->delete($id);
    }
}