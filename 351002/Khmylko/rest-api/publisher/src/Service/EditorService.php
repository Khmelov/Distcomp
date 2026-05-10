<?php
namespace App\Service;

use App\Exception\ConflictException;
use App\Repository\EditorRepository;
use App\Exception\ApiException;
use App\Exception\ValidationException;
use App\Exception\NotFoundException;

class EditorService {
    private EditorRepository $repo;

    public function __construct(EditorRepository $repo) {
        $this->repo = $repo;
    }

    public function getById(int $id): array {
        $editor = $this->repo->findById($id);
        if (!$editor) {
            throw new NotFoundException('Editor', $id);
        }
        // Убираем пароль из ответа
        unset($editor['password']);
        return $editor;
    }

    public function findAll(int $page = 1, int $limit = 10, string $sortBy = 'id', string $order = 'ASC'): array {
        $editors = $this->repo->findAll([], $sortBy, $order, $page, $limit);
        // Убираем пароли
        foreach ($editors as &$editor) {
            unset($editor['password']);
        }
        return $editors;
    }
    public function getAll(): array {
        $editors = $this->repo->findAll();
        foreach ($editors as &$editor) {
            unset($editor['password']);
        }
        return $editors;
    }
    public function createFromRegistrationRequest(array $data): array {
        $normalized = [
            'login' => (string)($data['login'] ?? ''),
            'password' => (string)($data['password'] ?? ''),
            'firstname' => (string)($data['firstName'] ?? $data['firstname'] ?? ''),
            'lastname' => (string)($data['lastName'] ?? $data['lastname'] ?? ''),
            'role' => (string)($data['role'] ?? 'CUSTOMER'),
        ];
        if (!in_array($normalized['role'], ['ADMIN', 'CUSTOMER'], true)) {
            throw new ValidationException('role must be ADMIN or CUSTOMER');
        }
        return $this->create($normalized);
    }

    public function create(array $data): array {
        if (empty($data['login']) || strlen($data['login']) < 2 || strlen($data['login']) > 64) {
            throw new ValidationException("Login must be 2-64 characters");
        }

        // Валидация пароля
        if (empty($data['password']) || strlen($data['password']) < 8 || strlen($data['password']) > 128) {
            throw new ValidationException("Password must be 8-128 characters");
        }

        // Валидация firstname
        if (empty($data['firstname']) || strlen($data['firstname']) < 2 || strlen($data['firstname']) > 64) {
            throw new ValidationException("Firstname must be 2-64 characters");
        }

        // Валидация lastname
        if (empty($data['lastname']) || strlen($data['lastname']) < 2 || strlen($data['lastname']) > 64) {
            throw new ValidationException("Lastname must be 2-64 characters");
        }

        $existing = $this->repo->findByLogin($data['login']);
        if ($existing) {
            throw new ConflictException("Login '{$data['login']}' already exists");
        }

        // Хешируем пароль
        $data['password'] = password_hash($data['password'], PASSWORD_BCRYPT);

        $editor = $this->repo->create($data);
        unset($editor['password']);
        return $editor;
    }

    public function update(int $id, array $data): array {
        $existing = $this->repo->findById($id);
        if (!$existing) {
            throw new NotFoundException('Editor', $id);
        }

        if (isset($data['login'])) {
            $existing['login'] = $data['login'];
        }
        if (isset($data['firstname'])) {
            $existing['firstname'] = $data['firstname'];
        }
        if (isset($data['lastname'])) {
            $existing['lastname'] = $data['lastname'];
        }
        if (isset($data['role'])) {
            $existing['role'] = $data['role'];
        }
        if (isset($data['password'])) {
            $existing['password'] = password_hash($data['password'], PASSWORD_BCRYPT);
        }

        $editor = $this->repo->update($id, $existing);
        unset($editor['password']);
        return $editor;
    }

    public function delete(int $id): void {
        if (!$this->repo->exists($id)) {
            throw new NotFoundException('Editor', $id);
        }
        $this->repo->delete($id);
    }

    private function validate(array $data): void {
        if (empty($data['login']) || strlen($data['login']) < 2 || strlen($data['login']) > 64) {
            throw new ValidationException("Login must be 2-64 characters");
        }
        if (empty($data['password']) || strlen($data['password']) < 8 || strlen($data['password']) > 128) {
            throw new ValidationException("Password must be 8-128 characters");
        }
        if (empty($data['firstname']) || strlen($data['firstname']) < 2 || strlen($data['firstname']) > 64) {
            throw new ValidationException("Firstname must be 2-64 characters");
        }
        if (empty($data['lastname']) || strlen($data['lastname']) < 2 || strlen($data['lastname']) > 64) {
            throw new ValidationException("Lastname must be 2-64 characters");
        }
    }
}