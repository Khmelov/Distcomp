<?php
namespace App\Service;

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

    public function create(array $data): array {
        $this->validate($data);

        // Хешируем пароль
        $data['password'] = password_hash($data['password'], PASSWORD_DEFAULT);

        $editor = $this->repo->create($data);
        unset($editor['password']);
        return $editor;
    }

    public function update(int $id, array $data): array {
        $existing = $this->repo->findById($id);
        if (!$existing) {
            throw new NotFoundException('Editor', $id);
        }

        if (isset($data['login'])) $existing['login'] = $data['login'];
        if (isset($data['firstname'])) $existing['firstname'] = $data['firstname'];
        if (isset($data['lastname'])) $existing['lastname'] = $data['lastname'];
        if (isset($data['password'])) {
            $existing['password'] = password_hash($data['password'], PASSWORD_DEFAULT);
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