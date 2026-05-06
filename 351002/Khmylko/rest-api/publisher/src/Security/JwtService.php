<?php
declare(strict_types=1);

namespace App\Security;

use App\Exception\ApiException;
use Firebase\JWT\JWT;
use Firebase\JWT\Key;

class JwtService {
    private string $secret;

    public function __construct(
        string $secret,
        private int $ttlSeconds = 28800
    ) {
        $this->secret = self::normalizeHmacSecret($secret);
    }

    public static function fromEnv(): self {
        $raw = getenv('JWT_SECRET');
        $raw = is_string($raw) && $raw !== '' ? $raw : 'distcomp-jwt-fallback-material';
        return new self($raw);
    }

    /**
     * firebase/php-jwt v7+ requires HS256 key length >= algorithm size (256 bits).
     * Short env values are stretched deterministically.
     */
    public static function normalizeHmacSecret(string $material): string {
        if (strlen($material) >= 32) {
            return $material;
        }
        return hash('sha256', $material, true);
    }

    /** @param array{sub:string,role:string,iat?:int,exp?:int} $claims */
    public function issue(string $login, string $role): string {
        $now = time();
        $payload = [
            'sub' => $login,
            'role' => $role,
            'iat' => $now,
            'exp' => $now + $this->ttlSeconds,
        ];
        return JWT::encode($payload, $this->secret, 'HS256');
    }

    /** @return array{sub:string,role:string} */
    public function parseBearer(string $headerValue): array {
        if (!str_starts_with($headerValue, 'Bearer ')) {
            throw new ApiException(401, 40101, 'Authorization header must be Bearer token');
        }
        $token = trim(substr($headerValue, 7));
        if ($token === '') {
            throw new ApiException(401, 40102, 'Missing access token');
        }
        try {
            $decoded = JWT::decode($token, new Key($this->secret, 'HS256'));
            $arr = (array)$decoded;
            $login = (string)($arr['sub'] ?? '');
            $role = (string)($arr['role'] ?? '');
            if ($login === '' || $role === '') {
                throw new ApiException(401, 40103, 'Invalid token payload');
            }
            return ['sub' => $login, 'role' => $role];
        } catch (\Throwable) {
            throw new ApiException(401, 40104, 'Invalid or expired token');
        }
    }
}
