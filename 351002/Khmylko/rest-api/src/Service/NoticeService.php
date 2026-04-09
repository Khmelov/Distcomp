<?php
namespace App\Service;

use App\Repository\NoticeRepository;
use App\Repository\TweetRepository;
use App\Exception\ValidationException;
use App\Exception\NotFoundException;

class NoticeService {
    private NoticeRepository $repository;
    private TweetRepository $tweetRepository;

    public function __construct(NoticeRepository $repository, TweetRepository $tweetRepo) {
        $this->repository = $repository;
        $this->tweetRepository = $tweetRepo;
    }

    public function findById(int $id): array {
        $notice = $this->repository->findById($id);

        if (!$notice) {
            throw new NotFoundException('Notice', $id);
        }

        return $this->map($notice);
    }

    public function findAll(int $page = 1, int $limit = 10, string $sortBy = 'id', string $order = 'ASC'): array {
        $notices = $this->repository->findAll([], $sortBy, $order, $page, $limit);
        return array_map([$this, 'map'], $notices);

    }

    public function create(array $data): array {

        // camelCase → snake_case
        $data['tweet_id'] = $data['tweet_id'] ?? $data['tweetId'] ?? null;

        if (!$data['tweet_id']) {
            throw new ValidationException("tweetId required");
        }

        if (!$this->tweetRepository->findById($data['tweet_id'])) {
            throw new ValidationException("Tweet not found");
        }

        $notice = $this->repository->create($data);

        $notice = $this->map($notice);
        return $notice;
    }

    public function update(int $id, array $data): array {
        $this->findById($id);

        if (isset($data['tweetId'])) {
            $data['tweet_id'] = $data['tweetId'];
        }

        $notice = $this->repository->update($id, $data);

        return $this->map($notice);
    }

    public function delete(int $id): void {
        $this->findById($id);
        $this->repository->delete($id);
    }
    public function getById(int $id): array {
        return $this->findById($id);
    }

    private function map(array $notice): array {
        if (isset($notice['tweet_id'])) {
            $notice['tweetId'] = $notice['tweet_id'];
            unset($notice['tweet_id']);
        }

        return $notice;
    }
}