<?php

namespace App\DTO;

use App\Entity\Issue;

class IssueResponseTo
{
    public readonly int $id;
    public readonly int $userId;
    public readonly string $title;
    public readonly string $content;

    public function __construct(
        Issue $issue
    ) {
        $this->id = $issue->getId();
        $this->userId = $issue->getUserId();
        $this->title = $issue->getTitle();
        $this->content = $issue->getContent();
    }
}