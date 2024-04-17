<?php

namespace App\Service;

use App\DTO\{CommentResponseTo, CommentRequestTo};
use App\Entity\Comment;
use App\CRUD\InMemoryCRUDRepository;

class CommentRepository {

    private InMemoryCRUDRepository $repo;

    function __construct(InMemoryCRUDRepository $storage) {

        $storage->connect(Comment::class);
        $this->repo = $storage;
    }

    public function create(Comment $comment) {

        $this->repo->create($comment);
        $this->repo->flush();

        return new CommentResponseTo($comment);
    }

    public function read(int $id) : ?Comment {

        $comment = $this->repo->findById($id);
        return $comment;
    }

    public function comment(int $id) : ?Comment {

        return $this->read($id);
    }

    public function all(): array {

        $comments = $this->repo->findAll();
        return $comments;
    }

    public function update() {

        $this->repo->flush();
    }

    public function delete(Comment $comment) {

        $this->repo->delete($comment);
        $this->repo->flush();
    }

}