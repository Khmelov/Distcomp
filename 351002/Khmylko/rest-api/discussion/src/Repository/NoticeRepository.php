<?php
namespace Discussion\Repository;

use Discussion\Exception\ApiException;

class NoticeRepository {
    private array $storage = [];
    private int $nextId = 1;
    private string $storageFile;

    public function __construct() {
        $this->storageFile = '/tmp/notices.json';
        $this->load();
    }

    private function load(): void {
        if (file_exists($this->storageFile)) {
            $data = json_decode(file_get_contents($this->storageFile), true);
            if ($data) {
                $this->storage = $data['storage'] ?? [];
                $this->nextId = $data['nextId'] ?? 1;
            }
        }
    }

    private function save(): void {
        file_put_contents($this->storageFile, json_encode([
            'storage' => $this->storage,
            'nextId' => $this->nextId
        ]));
    }

    public function create(array $data): array {
        $id = $this->nextId++;
        $notice = [
            'id' => $id,  // int
            'tweetId' => (int)$data['tweet_id'],
            'content' => $data['content']
        ];
        $this->storage[$id] = $notice;
        $this->save();
        return $notice;
    }

    public function findById(string $id): ?array {
        $idInt = (int)$id;
        if (!isset($this->storage[$idInt])) {
            return null;
        }
        $notice = $this->storage[$idInt];
        // Приводим к int
        $notice['id'] = (int)$notice['id'];
        $notice['tweetId'] = (int)$notice['tweetId'];
        return $notice;
    }

    public function findAll(): array {
        $result = [];
        foreach ($this->storage as $notice) {
            $result[] = [
                'id' => (int)$notice['id'],
                'tweetId' => (int)$notice['tweetId'],
                'content' => $notice['content']
            ];
        }
        return $result;
    }

    public function update(string $id, array $data): array {
        $idInt = (int)$id;
        if (!isset($this->storage[$idInt])) {
            throw new ApiException(404, 40401, "Notice not found");
        }
        if (isset($data['content'])) {
            $this->storage[$idInt]['content'] = $data['content'];
        }
        $this->save();
        $notice = $this->storage[$idInt];
        $notice['id'] = (int)$notice['id'];
        $notice['tweetId'] = (int)$notice['tweetId'];
        return $notice;
    }

    public function delete(string $id): void {
        $idInt = (int)$id;
        unset($this->storage[$idInt]);
        $this->save();
    }
}