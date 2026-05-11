<?php
declare(strict_types=1);

namespace Discussion\Kafka;

use Discussion\Repository\NoticeRepository;

class NoticeModerationService {
    /** @var string[] */
    private array $stopWords;

    public function __construct(
        private NoticeRepository $repository,
        ?array $stopWords = null
    ) {
        $this->stopWords = $stopWords ?? ['spam', 'forbidden', 'banned', 'hate'];
    }

    public function decideState(string $content): string {
        $lower = mb_strtolower($content);
        foreach ($this->stopWords as $w) {
            if ($w !== '' && str_contains($lower, mb_strtolower($w))) {
                return NoticeRepository::STATE_DECLINE;
            }
        }
        return NoticeRepository::STATE_APPROVE;
    }

    public function moderateAndPersist(int $noticeId, int $tweetId, string $content): array {
        $state = $this->decideState($content);
        return $this->repository->updateState($noticeId, $state);
    }
}
