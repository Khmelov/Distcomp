<?php

namespace App\DTO;

use Symfony\Component\Validator\Constraints as Assert;

class IssueRequestTo
{
    public function __construct(

        public readonly ?int $id,

        public readonly ?int $userId,

        #[Assert\NotBlank]
        #[Assert\Length(min: 2, max: 64)]
        public readonly string $title,

        #[Assert\NotBlank]
        #[Assert\Length(min: 4, max: 2048)]
        public readonly string $content,

        #[Assert\DateTime]
        public readonly ?\DateTimeInterface $created,

        #[Assert\DateTime]
        public readonly ?\DateTimeInterface $modified
    ) {
    }
}