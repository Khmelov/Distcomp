<?php

namespace App\DTO;

use Symfony\Component\Validator\Constraints as Assert;

class UserRequestTo
{
    public function __construct(

        public readonly ?int $id,

        #[Assert\NotBlank]
        #[Assert\Length(min: 2, max: 64, minMessage: 'Login minimal length is {{ limit }} chars.')]
        public readonly string $login,

        #[Assert\NotBlank]
        #[Assert\Length(min: 8, max: 128)]

        #[Assert\UserPassword]
        public readonly string $password,

        #[Assert\NotBlank]
        #[Assert\Length(min: 2, max: 64)]
        public readonly string $firstname,
        
        #[Assert\NotBlank]
        #[Assert\Length(min: 2, max: 64)]
        public readonly string $lastname,
    ) {
    }
}