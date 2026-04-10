<?php
namespace App\Repository;

class MarkerRepository extends AbstractRepository {
    protected string $table = 'tbl_marker';

    public function create(array $data): array {
        $stmt = $this->db->prepare("
            INSERT INTO {$this->table} (name, created, modified) 
            VALUES (:name, NOW(), NOW()) 
            RETURNING id, name
        ");
        $stmt->execute(['name' => $data['name']]);
        return $stmt->fetch();
    }

    public function update(int $id, array $data): array {
        $stmt = $this->db->prepare("
            UPDATE {$this->table} 
            SET name = :name, modified = NOW() 
            WHERE id = :id 
            RETURNING id, name
        ");
        $stmt->execute(['id' => $id, 'name' => $data['name']]);
        return $stmt->fetch();
    }

    public function findById(int $id): ?array {
        $stmt = $this->db->prepare("SELECT id, name FROM {$this->table} WHERE id = :id");
        $stmt->execute(['id' => $id]);
        return $stmt->fetch() ?: null;
    }

    public function findAll(array $filters = [], string $sortBy = 'id', string $order = 'ASC', int $page = 1, int $limit = 10): array {
        $offset = ($page - 1) * $limit;
        $sql = "SELECT id, name FROM {$this->table}";
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
            $type = is_int($val) ? \PDO::PARAM_INT : \PDO::PARAM_STR;
            $stmt->bindValue($key, $val, $type);
        }
        $stmt->execute();
        return $stmt->fetchAll();
    }
    public function findByName(string $name): ?array {
        $stmt = $this->db->prepare("SELECT id, name FROM {$this->table} WHERE name = :name");
        $stmt->execute(['name' => $name]);
        return $stmt->fetch() ?: null;
    }
}