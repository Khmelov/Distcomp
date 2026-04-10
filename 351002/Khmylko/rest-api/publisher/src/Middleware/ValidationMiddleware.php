<?php
declare(strict_types=1);

namespace App\Middleware;

use Psr\Http\Message\ServerRequestInterface as Request;
use Psr\Http\Server\RequestHandlerInterface as Handler;
use Psr\Http\Message\ResponseInterface;
use Respect\Validation\Validator as v;
use App\Exception\ValidationException;

class ValidationMiddleware
{
    private string $type;

    public function __construct(string $type)
    {
        $this->type = $type;
    }

    public function __invoke(Request $request, Handler $handler): ResponseInterface
    {
        $data = $request->getParsedBody();

        switch ($this->type) {
            case 'editor':
                $this->validateEditor($data);
                break;
            case 'tweet':
                $this->validateTweet($data);
                break;
            case 'marker':
                $this->validateMarker($data);
                break;
            case 'notice':
                $this->validateNotice($data);
                break;
        }

        return $handler->handle($request);
    }

    private function validateEditor(array $data): void
    {
        if (isset($data['login'])) {
            if (!v::stringType()->length(2, 64)->validate($data['login'])) {
                throw new ValidationException('Login must be between 2 and 64 characters');
            }
        }

        if (isset($data['password'])) {
            if (!v::stringType()->length(8, 128)->validate($data['password'])) {
                throw new ValidationException('Password must be between 8 and 128 characters');
            }
        }

        if (isset($data['firstname'])) {
            if (!v::stringType()->length(2, 64)->validate($data['firstname'])) {
                throw new ValidationException('Firstname must be between 2 and 64 characters');
            }
        }

        if (isset($data['lastname'])) {
            if (!v::stringType()->length(2, 64)->validate($data['lastname'])) {
                throw new ValidationException('Lastname must be between 2 and 64 characters');
            }
        }
    }

    private function validateTweet(array $data): void
    {
        if (isset($data['title'])) {
            if (!v::stringType()->length(2, 64)->validate($data['title'])) {
                throw new ValidationException('Title must be between 2 and 64 characters');
            }
        }

        if (isset($data['content'])) {
            if (!v::stringType()->length(4, 2048)->validate($data['content'])) {
                throw new ValidationException('Content must be between 4 and 2048 characters');
            }
        }
    }

    private function validateMarker(array $data): void
    {
        if (isset($data['name'])) {
            if (!v::stringType()->length(2, 32)->validate($data['name'])) {
                throw new ValidationException('Name must be between 2 and 32 characters');
            }
        }
    }

    private function validateNotice(array $data): void
    {
        if (isset($data['content'])) {
            if (!v::stringType()->length(4, 2048)->validate($data['content'])) {
                throw new ValidationException('Content must be between 4 and 2048 characters');
            }
        }

        if (isset($data['tweetId']) && !v::intVal()->positive()->validate($data['tweetId'])) {
            throw new ValidationException('TweetId must be a positive integer');
        }
    }
}