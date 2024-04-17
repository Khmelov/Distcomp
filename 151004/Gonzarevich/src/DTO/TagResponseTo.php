<?php

namespace App\DTO;

use App\Entity\Tag;

class TagResponseTo
{
    public readonly int $id;
    public readonly string $name;

    public function __construct(
        Tag $tag
    ) {
        $this->id = $tag->getId();
        $this->name = $tag->getName();
    }
}