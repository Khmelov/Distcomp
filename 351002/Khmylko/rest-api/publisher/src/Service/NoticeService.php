<?php
namespace App\Service;

use App\Exception\ApiException;
use App\Repository\TweetRepository;

class NoticeService {
    private string $discussionUrl;
    private TweetRepository $tweetRepository;

    public function __construct(TweetRepository $tweetRepository) {
        $this->tweetRepository = $tweetRepository;
        $this->discussionUrl = rtrim(getenv('DISCUSSION_URL') ?: 'http://discussion:24130', '/');
    }

    private function request(string $method, string $path, ?array $data = null): array {
        $url = $this->discussionUrl . '/api/v1.0/notices' . $path;
        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, $url);
        curl_setopt($ch, CURLOPT_CUSTOMREQUEST, $method);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_HTTPHEADER, ['Content-Type: application/json']);
        curl_setopt($ch, CURLOPT_TIMEOUT, 5);
        if ($data) {
            curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($data));
        }
        $response = curl_exec($ch);
        $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
        $error = curl_error($ch);
        curl_close($ch);

        if ($error) {
            throw new ApiException(500, 50001, "Discussion error: $error");
        }
        if ($httpCode >= 400) {
            $decoded = json_decode($response, true);
            $message = $decoded['errorMessage'] ?? "HTTP $httpCode";
            throw new ApiException($httpCode, $httpCode * 100 + 1, $message);
        }
        return json_decode($response, true) ?? [];
    }

    public function getById(int $id): array {
        return $this->request('GET', "/$id", null);
    }

    public function findAll(): array {
        return $this->request('GET', '', null);
    }

    public function create(array $data): array {
        $tweetId = $data['tweetId'] ?? $data['tweet_id'] ?? null;
        if (!$tweetId) {
            throw new ApiException(400, 40004, "tweetId is required");
        }

        // Проверка существования твита
        $tweet = $this->tweetRepository->findById($tweetId);
        if (!$tweet) {
            throw new ApiException(404, 40402, "Tweet not found");
        }

        $discussionData = [
            'tweet_id' => (string)$tweetId,
            'content' => $data['content'] ?? ''
        ];
        $result = $this->request('POST', '', $discussionData);

        if (isset($result['id'])) {
            $result['id'] = (int)$result['id'];
        }
        if (isset($result['tweetId'])) {
            $result['tweetId'] = (int)$result['tweetId'];
        }
        return $result;
    }

    public function update(int $id, array $data): array {
        return $this->request('PUT', "/$id", $data);
    }

    public function delete(int $id): void {
        $this->request('DELETE', "/$id", null);
    }
}