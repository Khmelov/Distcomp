<?php

namespace App\DTO;

use App\Entity\User;

class UserResponseTo
{
    public readonly int $id;
    public readonly string $login;
    public readonly string $password;
    public readonly string $firstname;
    public readonly string $lastname;

    public function __construct(
        User $user
    ) {
        $this->id = $user->getId();
        $this->login = $user->getLogin();
        $this->firstname = $user->getFirstname();
        $this->lastname = $user->getLastname();
    }
}