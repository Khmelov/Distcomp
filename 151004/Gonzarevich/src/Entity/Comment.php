<?php

namespace App\Entity;

class Comment
{

    private ?int $id = null;

    private ?int $issueId = null;

    private ?string $content = null;

    public function getId(): ?int
    {
        return $this->id;
    }

    public function setId(int $id): static
    {
        $this->id = $id;
        return $this;
    }

    public function getIssueId(): ?int
    {
        return $this->issueId;
    }

    public function setIssueId(int $issueId): static
    {
        $this->issueId = $issueId;

        return $this;
    }

    public function getContent(): ?string
    {
        return $this->content;
    }

    public function setContent(string $content): static
    {
        $this->content = $content;

        return $this;
    }

    public function populate(array $args) {

        list($temp, $this->issueId, $this->content) = $args;
    }
}
