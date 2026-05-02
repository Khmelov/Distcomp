<?php
declare(strict_types=1);

namespace App\Security;

use App\Exception\ApiException;
use App\Repository\EditorRepository;
use App\Repository\TweetRepository;
use App\Service\NoticeService;

class AccessPolicy {
    public function __construct(
        private ?array $user,
        private EditorRepository $editorRepository,
        private TweetRepository $tweetRepository,
        private NoticeService $noticeService
    ) {
    }

    /** @return array{id:int,login:string,firstname:string,lastname:string,role:string} */
    public function requireUser(): array {
        if ($this->user === null) {
            throw new ApiException(401, 40100, 'Authentication required');
        }
        return $this->user;
    }

    public function ensureAdmin(): void {
        $u = $this->requireUser();
        if ($u['role'] !== 'ADMIN') {
            throw new ApiException(403, 40301, 'Administrator role required');
        }
    }

    public function assertCustomerOwnsEditor(int $editorId): void {
        $u = $this->requireUser();
        if ($u['role'] === 'ADMIN') {
            return;
        }
        if ((int)$u['id'] !== $editorId) {
            throw new ApiException(403, 40302, 'You can only change your own profile');
        }
    }

    public function assertCustomerOwnsTweet(int $tweetId): void {
        $u = $this->requireUser();
        if ($u['role'] === 'ADMIN') {
            return;
        }
        $tweet = $this->tweetRepository->findById($tweetId);
        if (!$tweet || (int)$tweet['editorId'] !== (int)$u['id']) {
            throw new ApiException(403, 40303, 'Tweet is not owned by the current user');
        }
    }

    public function assertCanMutateMarker(): void {
        $u = $this->requireUser();
        if ($u['role'] !== 'ADMIN') {
            throw new ApiException(403, 40304, 'Only administrators can modify markers');
        }
    }

    public function assertCanMutateNotice(int $noticeId): void {
        $u = $this->requireUser();
        if ($u['role'] === 'ADMIN') {
            return;
        }
        $notice = $this->noticeService->getById($noticeId);
        $tweetId = (int)($notice['tweetId'] ?? 0);
        $this->assertCustomerOwnsTweet($tweetId);
    }

    public function assertTweetCreatePayload(array $data): void {
        $u = $this->requireUser();
        if ($u['role'] === 'ADMIN') {
            return;
        }
        $editorId = (int)($data['editorId'] ?? $data['editor_id'] ?? 0);
        if ($editorId !== (int)$u['id']) {
            throw new ApiException(403, 40305, 'Customers may only author content for themselves');
        }
    }

    public function assertNoticeCreatePayload(array $data): void {
        $u = $this->requireUser();
        if ($u['role'] === 'ADMIN') {
            return;
        }
        $tweetId = (int)($data['tweetId'] ?? $data['tweet_id'] ?? 0);
        if ($tweetId <= 0) {
            return;
        }
        $this->assertCustomerOwnsTweet($tweetId);
    }
}
