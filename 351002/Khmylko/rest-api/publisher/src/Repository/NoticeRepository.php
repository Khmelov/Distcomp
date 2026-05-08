<?php
namespace App\Repository;

class NoticeRepository extends AbstractRepository {
    protected string $table = 'tbl_notice';

    public function create(array $data): array {
        $stmt = $this->db->prepare("
        INSERT INTO {$this->table} (tweet_id, content)
        VALUES (:tweet_id, :content)
        RETURNING id, tweet_id, content
    ");

        $stmt->execute([
            'tweet_id' => $data['tweet_id'],
            'content' => $data['content']
        ]);

        return $stmt->fetch();
    }

    public function update(int $id, array $data): array {
        $stmt = $this->db->prepare("
        UPDATE {$this->table}
        SET content = :content,
            modified = CURRENT_TIMESTAMP
        WHERE id = :id
        RETURNING id, tweet_id, content
    ");

        $stmt->execute([
            'id' => $id,
            'content' => $data['content']
        ]);

        return $stmt->fetch();
    }
}