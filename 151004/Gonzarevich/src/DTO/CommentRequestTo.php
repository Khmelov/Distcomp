<?php

namespace App\DTO;

use Symfony\Component\Validator\Constraints as Assert;

class CommentRequestTo
{
    public function __construct(

        public readonly ?int $id,

        public readonly ?int $issueId,

        #[Assert\NotBlank]
        #[Assert\Length(min: 4, max: 2048)]
        public readonly string $content
    ) {
    }
}