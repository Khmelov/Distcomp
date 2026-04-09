<?php
namespace App\Repository;

use PDO;
use App\Database\Database;

abstract class AbstractRepository {
    protected PDO $db;
    protected string $table;

    public function __construct() {
        $this->db = Database::getConnection();
    }

    public function findById(int $id) {
        $stmt = $this->db->prepare("SELECT * FROM {$this->table} WHERE id = :id");
        $stmt->execute(['id' => $id]);
        return $stmt->fetch();
    }

    public function findAll(array $filters = [], string $sortBy = 'id', string $order = 'ASC', int $page = 1, int $limit = 10): array {
        $offset = ($page - 1) * $limit;
        $sql = "SELECT * FROM {$this->table}";
        $params = [];

        if (!empty($filters)) {
            $where = [];
            foreach ($filters as $key => $val) {
                $where[] = "{$key} ILIKE :{$key}";
                $params[$key] = "%$val%";
            }
            $sql .= " WHERE " . implode(" AND ", $where);
        }

        $order = strtoupper($order) === 'DESC' ? 'DESC' : 'ASC';
        $sql .= " ORDER BY " . preg_replace('/[^a-z0-9_]/i', '', $sortBy) . " $order";
        $sql .= " LIMIT :limit OFFSET :offset";

        $params['limit'] = $limit;
        $params['offset'] = $offset;

        $stmt = $this->db->prepare($sql);
        foreach ($params as $key => $val) {
            $type = is_int($val) ? PDO::PARAM_INT : PDO::PARAM_STR;
            $stmt->bindValue($key, $val, $type);
        }
        $stmt->execute();
        return $stmt->fetchAll();
    }

    public function exists(int $id): bool {
        $stmt = $this->db->prepare("SELECT 1 FROM {$this->table} WHERE id = :id");
        $stmt->execute(['id' => $id]);
        return (bool)$stmt->fetch();
    }

    public function delete(int $id): void {
        $stmt = $this->db->prepare("DELETE FROM {$this->table} WHERE id = :id");
        $stmt->execute(['id' => $id]);
    }
}