<?php
declare(strict_types=1);

namespace App\App;

use App\Cache\RedisCache;
use App\Controller\EditorController;
use App\Controller\MarkerController;
use App\Controller\NoticeController;
use App\Controller\TweetController;
use App\Exception\ApiException;
use App\Exception\ValidationException;
use App\Repository\EditorRepository;
use App\Repository\MarkerRepository;
use App\Repository\TweetRepository;
use App\Security\AccessPolicy;
use App\Security\AuthUserLoader;
use App\Security\JwtService;
use App\Service\EditorService;
use App\Service\MarkerService;
use App\Service\NoticeService;
use App\Service\TweetService;

final class FrontController {
    private const CACHE_TTL = 120;

    public function __construct(
        private RedisCache $cache,
        private JwtService $jwt,
        private AuthUserLoader $authUserLoader,
        private EditorRepository $editorRepository,
        private TweetRepository $tweetRepository,
        private EditorService $editorService,
        private TweetService $tweetService,
        private MarkerService $markerService,
        private NoticeService $noticeService
    ) {
    }

    public static function fromEnvironment(): self {
        $cache = RedisCache::createFromEnv();
        $editorRepo = new EditorRepository();
        $tweetRepo = new TweetRepository();
        $markerRepo = new MarkerRepository();
        $markerService = new MarkerService($markerRepo);
        $editorService = new EditorService($editorRepo);
        $tweetService = new TweetService($tweetRepo, $editorRepo, $markerService);
        $noticeService = new NoticeService($tweetRepo);
        return new self(
            $cache,
            JwtService::fromEnv(),
            new AuthUserLoader($editorRepo),
            $editorRepo,
            $tweetRepo,
            $editorService,
            $tweetService,
            $markerService,
            $noticeService
        );
    }

    public function run(): void {
        $method = $_SERVER['REQUEST_METHOD'] ?? 'GET';
        if ($method === 'OPTIONS') {
            http_response_code(200);
            return;
        }

        $uri = (string)(parse_url($_SERVER['REQUEST_URI'] ?? '/', PHP_URL_PATH) ?? '/');
        $parts = array_values(array_filter(explode('/', trim($uri, '/'))));
        if (count($parts) < 2 || $parts[0] !== 'api') {
            throw new ApiException(404, 40400, 'Endpoint not found');
        }

        $version = $parts[1];
        if (!in_array($version, ['v1.0', 'v2.0'], true)) {
            throw new ApiException(404, 40400, 'Endpoint not found');
        }

        if ($version === 'v2.0' && ($parts[2] ?? '') === 'login' && $method === 'POST') {
            $this->jsonResponse(200, $this->handleLogin());
            return;
        }

        if ($version === 'v2.0' && ($parts[2] ?? '') === 'me' && $method === 'GET') {
            $header = $_SERVER['HTTP_AUTHORIZATION'] ?? '';
            if ($header === '') {
                throw new ApiException(401, 40105, 'Authentication required');
            }
            $claims = $this->jwt->parseBearer($header);
            $user = $this->authUserLoader->loadFromTokenClaims($claims);
            if ($user === null) {
                throw new ApiException(401, 40106, 'User no longer exists');
            }
            $this->jsonResponse(200, $user);
            return;
        }

        $authUser = null;
        if ($version === 'v2.0') {
            $isRegister = ($parts[2] ?? '') === 'editors' && $method === 'POST' && count($parts) === 3;
            if (!$isRegister) {
                $authUser = $this->resolveV2User();
            }
        }

        $resource = $parts[2] ?? null;
        $id = isset($parts[3]) ? (int)$parts[3] : null;
        if ($resource === null || !in_array($resource, ['editors', 'tweets', 'markers', 'notices'], true)) {
            throw new ApiException(404, 40400, 'Resource not found');
        }

        $policy = $this->accessPolicy($authUser);
        $out = $this->dispatchCrud($version, $resource, $id, $method, $policy);
        if ($out === null) {
            return;
        }
        $code = $method === 'POST' ? 201 : 200;
        $this->jsonResponse($code, $out);
    }

    private function accessPolicy(?array $user): AccessPolicy {
        return new AccessPolicy(
            $user,
            $this->editorRepository,
            $this->tweetRepository,
            $this->noticeService
        );
    }

    /** @return array<string, mixed>|null */
    private function dispatchCrud(string $version, string $resource, ?int $id, string $method, AccessPolicy $policy): ?array {
        if ($version === 'v2.0' && !($resource === 'editors' && $method === 'POST')) {
            $policy->requireUser();
        }

        $useCache = $version === 'v1.0' && $this->cache->isEnabled();
        $vPrefix = $version;

        if ($resource === 'editors') {
            $controller = new EditorController($this->editorService);
            return match ($method) {
                'GET' => $id
                    ? ($useCache
                        ? $this->cache->rememberJson("{$vPrefix}:editor:{$id}", self::CACHE_TTL, fn () => $controller->getById($id))
                        : $controller->getById($id))
                    : ($useCache
                        ? $this->cache->rememberJson("{$vPrefix}:editors:list", self::CACHE_TTL, fn () => $controller->getAll())
                        : $controller->getAll()),
                'POST' => $this->handleEditorCreate($version, $policy),
                'PUT' => $this->handleEditorPut($version, $id, $policy, $controller),
                'DELETE' => $this->handleEditorDelete($version, $id, $policy, $controller),
                default => throw new ApiException(405, 40500, 'Method not allowed'),
            };
        }

        if ($resource === 'tweets') {
            $controller = new TweetController($this->tweetService);
            return match ($method) {
                'GET' => $id
                    ? ($useCache
                        ? $this->cache->rememberJson("{$vPrefix}:tweet:{$id}", self::CACHE_TTL, fn () => $controller->getById($id))
                        : $controller->getById($id))
                    : ($useCache
                        ? $this->cache->rememberJson("{$vPrefix}:tweets:list", self::CACHE_TTL, fn () => $controller->getAll())
                        : $controller->getAll()),
                'POST' => $this->handleTweetPost($version, $policy, $controller),
                'PUT' => $this->handleTweetPut($version, $id, $policy, $controller),
                'DELETE' => $this->handleTweetDelete($version, $id, $policy, $controller),
                default => throw new ApiException(405, 40500, 'Method not allowed'),
            };
        }

        if ($resource === 'markers') {
            $controller = new MarkerController($this->markerService);
            return match ($method) {
                'GET' => $id
                    ? ($useCache
                        ? $this->cache->rememberJson("{$vPrefix}:marker:{$id}", self::CACHE_TTL, fn () => $controller->getById($id))
                        : $controller->getById($id))
                    : ($useCache
                        ? $this->cache->rememberJson("{$vPrefix}:markers:list", self::CACHE_TTL, fn () => $controller->getAll())
                        : $controller->getAll()),
                'POST' => $this->handleMarkerPost($version, $policy, $controller),
                'PUT' => $this->handleMarkerPut($version, $id, $policy, $controller),
                'DELETE' => $this->handleMarkerDelete($version, $id, $policy, $controller),
                default => throw new ApiException(405, 40500, 'Method not allowed'),
            };
        }

        $controller = new NoticeController($this->noticeService);
        return match ($method) {
            'GET' => $id
                ? ($useCache
                    ? $this->cache->rememberJson("{$vPrefix}:notice:{$id}", self::CACHE_TTL, fn () => $controller->getById($id))
                    : $controller->getById($id))
                : ($useCache
                    ? $this->cache->rememberJson("{$vPrefix}:notices:list", self::CACHE_TTL, fn () => $controller->getAll())
                    : $controller->getAll()),
            'POST' => $this->handleNoticePost($version, $policy, $controller),
            'PUT' => $this->handleNoticePut($version, $id, $policy, $controller),
            'DELETE' => $this->handleNoticeDelete($version, $id, $policy, $controller),
            default => throw new ApiException(405, 40500, 'Method not allowed'),
        };
    }

    private function handleEditorCreate(string $version, AccessPolicy $policy): array {
        $data = $this->jsonBody();
        if ($version === 'v2.0') {
            $created = $this->editorService->createFromRegistrationRequest($data);
            $this->cache->invalidateEditorsList($version);
            return $created;
        }
        $created = $this->editorService->create($data);
        $this->cache->invalidateEditorsList($version);
        return $created;
    }

    private function handleEditorPut(string $version, ?int $id, AccessPolicy $policy, EditorController $controller): array {
        if (!$id) {
            throw new ApiException(400, 40001, 'ID required');
        }
        $data = $this->jsonBody();
        if ($version === 'v2.0') {
            $policy->assertCustomerOwnsEditor($id);
            if (isset($data['role'])) {
                $user = $policy->requireUser();
                if ($user['role'] !== 'ADMIN' && $data['role'] !== $user['role']) {
                    throw new ApiException(403, 40306, 'Only administrators may change roles');
                }
                if (!in_array($data['role'], ['ADMIN', 'CUSTOMER'], true)) {
                    throw new ValidationException('role must be ADMIN or CUSTOMER');
                }
            }
        }
        $updated = $controller->update($id, $data);
        $this->cache->invalidateEditor($version, $id);
        return $updated;
    }

    private function handleEditorDelete(string $version, ?int $id, AccessPolicy $policy, EditorController $controller): ?array {
        if (!$id) {
            throw new ApiException(400, 40001, 'ID required');
        }
        if ($version === 'v2.0') {
            $policy->assertCustomerOwnsEditor($id);
        }
        $controller->delete($id);
        $this->cache->invalidateEditor($version, $id);
        http_response_code(204);
        return null;
    }

    private function handleTweetPost(string $version, AccessPolicy $policy, TweetController $controller): array {
        $data = $this->jsonBody();
        if ($version === 'v2.0') {
            $policy->assertTweetCreatePayload($data);
        }
        $created = $controller->create($data);
        $this->cache->invalidateTweetsList($version);
        return $created;
    }

    private function handleTweetPut(string $version, ?int $id, AccessPolicy $policy, TweetController $controller): array {
        if (!$id) {
            throw new ApiException(400, 40001, 'ID required');
        }
        if ($version === 'v2.0') {
            $policy->assertCustomerOwnsTweet($id);
        }
        $data = $this->jsonBody();
        $updated = $controller->update($id, $data);
        $this->cache->invalidateTweet($version, $id);
        return $updated;
    }

    private function handleTweetDelete(string $version, ?int $id, AccessPolicy $policy, TweetController $controller): ?array {
        if (!$id) {
            throw new ApiException(400, 40001, 'ID required');
        }
        if ($version === 'v2.0') {
            $policy->assertCustomerOwnsTweet($id);
        }
        $controller->delete($id);
        $this->cache->invalidateTweet($version, $id);
        http_response_code(204);
        return null;
    }

    private function handleMarkerPost(string $version, AccessPolicy $policy, MarkerController $controller): array {
        if ($version === 'v2.0') {
            $policy->assertCanMutateMarker();
        }
        $data = $this->jsonBody();
        $created = $controller->create($data);
        $this->cache->invalidateMarkersList($version);
        return $created;
    }

    private function handleMarkerPut(string $version, ?int $id, AccessPolicy $policy, MarkerController $controller): array {
        if (!$id) {
            throw new ApiException(400, 40001, 'ID required');
        }
        if ($version === 'v2.0') {
            $policy->assertCanMutateMarker();
        }
        $data = $this->jsonBody();
        $updated = $controller->update($id, $data);
        $this->cache->invalidateMarker($version, $id);
        return $updated;
    }

    private function handleMarkerDelete(string $version, ?int $id, AccessPolicy $policy, MarkerController $controller): ?array {
        if (!$id) {
            throw new ApiException(400, 40001, 'ID required');
        }
        if ($version === 'v2.0') {
            $policy->assertCanMutateMarker();
        }
        $controller->delete($id);
        $this->cache->invalidateMarker($version, $id);
        http_response_code(204);
        return null;
    }

    private function handleNoticePost(string $version, AccessPolicy $policy, NoticeController $controller): array {
        $data = $this->jsonBody();
        if ($version === 'v2.0') {
            $policy->assertNoticeCreatePayload($data);
        }
        $created = $controller->create($data);
        $this->cache->invalidateNoticesList($version);
        return $created;
    }

    private function handleNoticePut(string $version, ?int $id, AccessPolicy $policy, NoticeController $controller): array {
        if (!$id) {
            throw new ApiException(400, 40001, 'ID required');
        }
        if ($version === 'v2.0') {
            $policy->assertCanMutateNotice($id);
        }
        $data = $this->jsonBody();
        $updated = $controller->update($id, $data);
        $this->cache->invalidateNotice($version, $id);
        return $updated;
    }

    private function handleNoticeDelete(string $version, ?int $id, AccessPolicy $policy, NoticeController $controller): ?array {
        if (!$id) {
            throw new ApiException(400, 40001, 'ID required');
        }
        if ($version === 'v2.0') {
            $policy->assertCanMutateNotice($id);
        }
        $controller->delete($id);
        $this->cache->invalidateNotice($version, $id);
        http_response_code(204);
        return null;
    }

    /** @return array<string, mixed> */
    private function handleLogin(): array {
        $body = $this->jsonBody();
        $login = (string)($body['login'] ?? '');
        $password = (string)($body['password'] ?? '');
        if ($login === '' || $password === '') {
            throw new ApiException(400, 40010, 'login and password are required');
        }
        $row = $this->editorRepository->findByLoginWithCredentials($login);
        if (!$row || !password_verify($password, (string)$row['password'])) {
            throw new ApiException(401, 40110, 'Invalid credentials');
        }
        $role = (string)($row['role'] ?? 'CUSTOMER');
        $token = $this->jwt->issue((string)$row['login'], $role);
        return [
            'access_token' => $token,
            'token_type' => 'Bearer',
        ];
    }

    /** @return array<string, mixed> */
    private function jsonBody(): array {
        $raw = file_get_contents('php://input') ?: '';
        if (trim($raw) === '') {
            return [];
        }
        try {
            $data = json_decode($raw, true, 512, JSON_THROW_ON_ERROR);
            return is_array($data) ? $data : [];
        } catch (\Throwable) {
            throw new ApiException(400, 40011, 'Invalid JSON payload');
        }
    }

    private function jsonResponse(int $code, array $payload): void {
        http_response_code($code);
        echo json_encode($payload, JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT);
    }

    /** @return array<string, mixed>|null */
    private function resolveV2User(): ?array {
        $header = $_SERVER['HTTP_AUTHORIZATION'] ?? '';
        if ($header === '') {
            return null;
        }
        $claims = $this->jwt->parseBearer($header);
        return $this->authUserLoader->loadFromTokenClaims($claims);
    }
}
