<?php

namespace App\Service;

use App\DTO\{IssueResponseTo, IssueRequestTo};
use App\Entity\Issue;
use App\CRUD\InMemoryCRUDRepository;

class IssueRepository {

    private InMemoryCRUDRepository $repo;

    function __construct(InMemoryCRUDRepository $storage) {

        $storage->connect(Issue::class);
        $this->repo = $storage;
    }

    public function create(Issue $issue) {

        $this->repo->create($issue);
        $this->repo->flush();

        return new IssueResponseTo($issue);
    }

    public function read(int $id) : ?Issue {

        $issue = $this->repo->findById($id);
        return $issue;
    }

    public function issue(int $id) : ?Issue {

        return $this->read($id);
    }

    public function all(): array {

        $issues = $this->repo->findAll();
        return $issues;
    }

    public function update() {

        $this->repo->flush();
    }

    public function delete(Issue $issue) {

        $this->repo->delete($issue);
        $this->repo->flush();
    }

}