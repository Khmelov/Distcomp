<?php
namespace Discussion\Repository;

use Discussion\Exception\ApiException;

class NoticeRepository {
    private array $storage = [];
    private int $nextId = 1;
    private string $storageFile;

    public const STATE_PENDING = 'PENDING';
    public const STATE_APPROVE = 'APPROVE';
    public const STATE_DECLINE = 'DECLINE';

    public function __construct() {
        $this->storageFile = getenv('NOTICE_STORAGE_FILE') ?: '/tmp/notices.json';
        $this->load();
    }

    private function load(): void {
        if (file_exists($this->storageFile)) {
            $data = json_decode(file_get_contents($this->storageFile), true);
            if ($data) {
                $this->storage = $data['storage'] ?? [];
                $this->nextId = (int)($data['nextId'] ?? 1);
            }
        }
    }

    private function save(): void {
        file_put_contents($this->storageFile, json_encode([
            'storage' => $this->storage,
            'nextId' => $this->nextId,
        ], JSON_THROW_ON_ERROR));
    }

    private function normalize(array $notice): array {
        $notice['id'] = (int)$notice['id'];
        $notice['tweetId'] = (int)$notice['tweetId'];
        if (!isset($notice['state'])) {
            $notice['state'] = self::STATE_PENDING;
        }
        return $notice;
    }

    public function create(array $data): array {
        $id = $this->nextId++;
        $notice = [
            'id' => $id,
            'tweetId' => (int)$data['tweet_id'],
            'content' => $data['content'],
            'state' => $data['state'] ?? self::STATE_PENDING,
        ];
        $this->storage[$id] = $notice;
        $this->save();
        return $this->normalize($notice);
    }

    public function findById(string $id): ?array {
        $idInt = (int)$id;
        if (!isset($this->storage[$idInt])) {
            return null;
        }
        return $this->normalize($this->storage[$idInt]);
    }

    public function findAll(): array {
        $result = [];
        foreach ($this->storage as $notice) {
            $result[] = $this->normalize($notice);
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
        if (isset($data['state'])) {
            $this->storage[$idInt]['state'] = $data['state'];
        }
        $this->save();
        return $this->normalize($this->storage[$idInt]);
    }

    public function updateState(int $id, string $state): array {
        if (!isset($this->storage[$id])) {
            throw new ApiException(404, 40401, "Notice not found");
        }
        $this->storage[$id]['state'] = $state;
        $this->save();
        return $this->normalize($this->storage[$id]);
    }

    public function delete(string $id): void {
        $idInt = (int)$id;
        unset($this->storage[$idInt]);
        $this->save();
    }
}
