<?php

namespace App\Service;

use App\Entity\User;
use App\CRUD\InMemoryCRUDRepository;

class UserRepository {

    private InMemoryCRUDRepository $repo;

    function __construct(InMemoryCRUDRepository $storage) {

        $storage->connect(User::class);
        $this->repo = $storage;
    }

    public function persist(User $user) {

        $this->repo->create($user);
        $this->repo->flush();

        return $user;
    }

    public function read(int $id) : ?User {

        $user = $this->repo->findById($id);
        return $user;
    }

    public function user(int $id) : ?User {

        return $this->read($id);
    }

    public function all(): array {

        $users = $this->repo->findAll();
        return $users;
    }

    public function update() {

        $this->repo->flush();
    }

    public function delete(User $user) {

        $this->repo->delete($user);
        $this->repo->flush();
    }

}