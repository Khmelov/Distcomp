<?php
namespace App\Repository;

class EditorRepository extends AbstractRepository {
    protected string $table = 'tbl_editor';

    public function create(array $data): array {
        $sql = "INSERT INTO {$this->table} (login, password, firstname, lastname, created, modified) 
            VALUES (:login, :password, :firstname, :lastname, NOW(), NOW()) 
            RETURNING id, login, firstname, lastname";  // ← только нужные поля
        $stmt = $this->db->prepare($sql);
        $stmt->execute([
            'login' => $data['login'],
            'password' => $data['password'],
            'firstname' => $data['firstname'],
            'lastname' => $data['lastname']
        ]);
        return $stmt->fetch();
    }

    public function update(int $id, array $data): array {
        $sql = "UPDATE {$this->table} SET login = :login, firstname = :firstname, 
            lastname = :lastname, modified = NOW() WHERE id = :id 
            RETURNING id, login, firstname, lastname";  // ← только нужные поля
        $stmt = $this->db->prepare($sql);
        $stmt->execute([
            'id' => $id,
            'login' => $data['login'],
            'firstname' => $data['firstname'],
            'lastname' => $data['lastname']
        ]);
        return $stmt->fetch();
    }
}