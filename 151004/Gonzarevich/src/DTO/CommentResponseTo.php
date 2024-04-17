<?php

namespace App\DTO;

use App\Entity\Comment;

class CommentResponseTo
{
    public readonly int $id;
    public readonly int $issueId;
    public readonly string $content;

    public function __construct(
        Comment $issue
    ) {
        $this->id = $issue->getId();
        $this->issueId = $issue->getIssueId();
        $this->content = $issue->getContent();
    }
}