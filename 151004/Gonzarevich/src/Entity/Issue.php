<?php

namespace App\Entity;

class Issue
{

    private ?int $id = null;

    private ?int $userId = null;

    private ?string $title = null;

    private ?string $content = null;

    private ?\DateTimeInterface $created = null;

    private ?\DateTimeInterface $modified = null;

    public function getId(): ?int
    {
        return $this->id;
    }

    public function setId(int $id): static
    {
        $this->id = $id;
        return $this;
    }

    public function getUserId(): ?int
    {
        return $this->userId;
    }

    public function setUserId(int $userId): static
    {
        $this->userId = $userId;

        return $this;
    }

    public function getTitle(): ?string
    {
        return $this->title;
    }

    public function setTitle(string $title): static
    {
        $this->title = $title;

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

    public function getCreated(): ?\DateTimeInterface
    {
        return $this->created;
    }

    public function setCreated(\DateTimeInterface $created): static
    {
        $this->created = $created;

        return $this;
    }

    public function getModified(): ?\DateTimeInterface
    {
        return $this->modified;
    }

    public function setModified(\DateTimeInterface $modified): static
    {
        $this->modified = $modified;

        return $this;
    }

    public function populate(array $args) {

        list($temp, $this->userId, $this->title, $this->content, $this->created, $this->modified) = $args;
    }
}
