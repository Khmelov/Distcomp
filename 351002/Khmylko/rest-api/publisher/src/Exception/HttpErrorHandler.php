<?php
declare(strict_types=1);

namespace App\Exception;

use Psr\Http\Message\ServerRequestInterface;
use Psr\Http\Message\ResponseInterface;
use Slim\Exception\HttpNotFoundException;
use Slim\Handlers\ErrorHandler;

class HttpErrorHandler extends ErrorHandler
{
    protected function respond(): ResponseInterface
    {
        $exception = $this->exception;
        $statusCode = 500;
        $errorCode = 50001;
        $message = 'Internal server error';

        if ($exception instanceof NotFoundException) {
            $statusCode = 404;
            $errorCode = 40401;
            $message = $exception->getMessage();
        } elseif ($exception instanceof ValidationException) {
            $statusCode = 400;
            $errorCode = 40001;
            $message = $exception->getMessage();
        } elseif ($exception instanceof HttpNotFoundException) {
            $statusCode = 404;
            $errorCode = 40402;
            $message = 'Resource not found';
        }

        $errorResponse = [
            'errorMessage' => $message,
            'errorCode' => $errorCode
        ];

        $response = $this->responseFactory->createResponse($statusCode);
        $response->getBody()->write(json_encode($errorResponse));

        return $response->withHeader('Content-Type', 'application/json');
    }
}