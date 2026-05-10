<?php
declare(strict_types=1);

namespace App\Security;

use App\Repository\EditorRepository;

class AuthUserLoader {
    public function __construct(private EditorRepository $editorRepository) {
    }

    /** @return array{id:int,login:string,firstname:string,lastname:string,role:string}|null */
    public function loadFromTokenClaims(array $claims): ?array {
        $login = $claims['sub'] ?? '';
        if ($login === '') {
            return null;
        }
        $row = $this->editorRepository->findByLogin($login);
        if (!$row) {
            return null;
        }
        $row['role'] = (string)($row['role'] ?? 'CUSTOMER');
        return $row;
    }
}
