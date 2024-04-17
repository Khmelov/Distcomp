<?php

namespace App\Service;

use App\DTO\{TagResponseTo, TagRequestTo};
use App\Entity\Tag;
use App\CRUD\InMemoryCRUDRepository;

class TagRepository {

    private InMemoryCRUDRepository $repo;

    function __construct(InMemoryCRUDRepository $storage) {

        $storage->connect(Tag::class);
        $this->repo = $storage;
    }

    public function create(Tag $tag) {

        $this->repo->create($tag);
        $this->repo->flush();

        return new TagResponseTo($tag);
    }

    public function read(int $id) : ?Tag {

        $tag = $this->repo->findById($id);
        return $tag;
    }

    public function tag(int $id) : ?Tag {

        return $this->read($id);
    }

    public function all(): array {

        $tags = $this->repo->findAll();
        return $tags;
    }

    public function update() {

        $this->repo->flush();
    }

    public function delete(Tag $tag) {

        $this->repo->delete($tag);
        $this->repo->flush();
    }

}