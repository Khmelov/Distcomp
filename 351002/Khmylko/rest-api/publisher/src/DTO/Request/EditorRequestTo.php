<?php
namespace App\DTO\Request;

class EditorRequestTo {
    private string $login;
    private string $password;
    private string $firstname;
    private string $lastname;

    public function __construct(array $data) {
        $this->login = $data['login'] ?? '';
        $this->password = $data['password'] ?? '';
        $this->firstname = $data['firstname'] ?? '';
        $this->lastname = $data['lastname'] ?? '';
    }

    public function getLogin(): string { return $this->login; }
    public function getPassword(): string { return $this->password; }
    public function getFirstname(): string { return $this->firstname; }
    public function getLastname(): string { return $this->lastname; }
}