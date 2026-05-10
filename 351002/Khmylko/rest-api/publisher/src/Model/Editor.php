<?php
namespace App\Model;

class Editor {
    private ?int $id = null;
    private string $login;
    private string $password;
    private string $firstname;
    private string $lastname;
    private string $created;
    private string $modified;

    public function __construct() {
        $this->created = date('Y-m-d H:i:s');
        $this->modified = date('Y-m-d H:i:s');
    }

    public function getId(): ?int { return $this->id; }
    public function setId(int $id): self { $this->id = $id; return $this; }
    public function getLogin(): string { return $this->login; }
    public function setLogin(string $login): self { $this->login = $login; return $this; }
    public function getPassword(): string { return $this->password; }
    public function setPassword(string $password): self { $this->password = password_hash($password, PASSWORD_DEFAULT); return $this; }
    public function getFirstname(): string { return $this->firstname; }
    public function setFirstname(string $firstname): self { $this->firstname = $firstname; return $this; }
    public function getLastname(): string { return $this->lastname; }
    public function setLastname(string $lastname): self { $this->lastname = $lastname; return $this; }

    public function toArray(): array {
        return [
            'id' => $this->id,
            'login' => $this->login,
            'firstname' => $this->firstname,
            'lastname' => $this->lastname
        ];
    }
}