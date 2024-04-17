<?php

namespace App\DTO;

use Symfony\Component\Validator\Constraints as Assert;

class TagRequestTo
{
    public function __construct(

        public readonly ?int $id,

        #[Assert\NotBlank]
        #[Assert\Length(min: 2, max: 32)]
        public readonly string $name,
    ) {
    }
}