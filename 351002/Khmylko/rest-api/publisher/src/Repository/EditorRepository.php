<?php
namespace App\Repository;

class EditorRepository extends AbstractRepository {
    protected string $table = 'tbl_editor';

    public function create(array $data): array {
        $role = $data['role'] ?? 'CUSTOMER';
        $sql = "INSERT INTO {$this->table} (login, password, firstname, lastname, role, created, modified) 
            VALUES (:login, :password, :firstname, :lastname, :role, NOW(), NOW()) 
            RETURNING id, login, firstname, lastname, role";
        $stmt = $this->db->prepare($sql);
        $stmt->execute([
            'login' => $data['login'],
            'password' => $data['password'],
            'firstname' => $data['firstname'],
            'lastname' => $data['lastname'],
            'role' => $role,
        ]);
        $row = $stmt->fetch();
        if ($row && isset($row['id'])) {
            $row['id'] = (int)$row['id'];
        }
        return $row;
    }
    public function findByLogin(string $login): ?array {
        $stmt = $this->db->prepare("SELECT id, login, firstname, lastname, role FROM {$this->table} WHERE login = :login");
        $stmt->execute(['login' => $login]);
        $row = $stmt->fetch() ?: null;
        if ($row && isset($row['id'])) {
            $row['id'] = (int)$row['id'];
        }
        return $row;
    }

    public function findByLoginWithCredentials(string $login): ?array {
        $stmt = $this->db->prepare("SELECT * FROM {$this->table} WHERE login = :login");
        $stmt->execute(['login' => $login]);
        $row = $stmt->fetch() ?: null;
        if ($row && isset($row['id'])) {
            $row['id'] = (int)$row['id'];
        }
        return $row;
    }
    public function update(int $id, array $data): array {
        $sets = ['login = :login', 'firstname = :firstname', 'lastname = :lastname', 'modified = NOW()'];
        $params = [
            'id' => $id,
            'login' => $data['login'],
            'firstname' => $data['firstname'],
            'lastname' => $data['lastname'],
        ];
        if (isset($data['password'])) {
            $sets[] = 'password = :password';
            $params['password'] = $data['password'];
        }
        if (isset($data['role'])) {
            $sets[] = 'role = :role';
            $params['role'] = $data['role'];
        }
        $sql = "UPDATE {$this->table} SET " . implode(', ', $sets) . " WHERE id = :id 
            RETURNING id, login, firstname, lastname, role";
        $stmt = $this->db->prepare($sql);
        $stmt->execute($params);
        $row = $stmt->fetch();
        if ($row && isset($row['id'])) {
            $row['id'] = (int)$row['id'];
        }
        return $row;
    }
}