<?php
declare(strict_types=1);

use Slim\App;
use App\Controller\EditorController;
use App\Controller\TweetController;
use App\Controller\MarkerController;
use App\Controller\NoticeController;
use App\Controller\TweetAdvancedController;
use App\Middleware\ValidationMiddleware;

return function (App $app, $editorService, $tweetService, $markerService, $noticeService) {
    $app->group('/api/v1.0', function ($group) use ($editorService, $tweetService, $markerService, $noticeService) {

        // СТАТИЧЕСКИЕ МАРШРУТЫ СНАЧАЛА
        $group->get('/tweets/search', function ($request, $response) use ($tweetService) {
            $controller = new TweetAdvancedController($tweetService, null, null, null);
            return $controller->searchTweets($request, $response);
        });

        $group->get('/tweets/{tweetId}/editor', function ($request, $response, $args) use ($tweetService, $editorService) {
            $controller = new TweetAdvancedController($tweetService, $editorService, null, null);
            return $controller->getEditorByTweetId($request, $response, $args);
        });

        $group->get('/tweets/{tweetId}/markers', function ($request, $response, $args) use ($tweetService, $markerService) {
            $controller = new TweetAdvancedController($tweetService, null, $markerService, null);
            return $controller->getMarkersByTweetId($request, $response, $args);
        });

        $group->get('/tweets/{tweetId}/notices', function ($request, $response, $args) use ($tweetService, $noticeService) {
            $controller = new TweetAdvancedController($tweetService, null, null, $noticeService);
            return $controller->getNoticesByTweetId($request, $response, $args);
        });

        // EDITOR ROUTES
        $group->get('/editors', function ($request, $response) use ($editorService) {
            $controller = new EditorController($editorService);
            return $controller->findAll($request, $response);
        });

        $group->post('/editors', function ($request, $response) use ($editorService) {
            $controller = new EditorController($editorService);
            return $controller->create($request, $response);
        })->add(new ValidationMiddleware('editor'));

        // TWEET ROUTES
        $group->get('/tweets', function ($request, $response) use ($tweetService) {
            $controller = new TweetController($tweetService, null, null, null);
            return $controller->findAll($request, $response);
        });

        $group->post('/tweets', function ($request, $response) use ($tweetService) {
            $controller = new TweetController($tweetService, null, null, null);
            return $controller->create($request, $response);
        })->add(new ValidationMiddleware('tweet'));

        // MARKER ROUTES
        $group->get('/markers', function ($request, $response) use ($markerService) {
            $controller = new MarkerController($markerService);
            return $controller->findAll($request, $response);
        });

        $group->post('/markers', function ($request, $response) use ($markerService) {
            $controller = new MarkerController($markerService);
            return $controller->create($request, $response);
        })->add(new ValidationMiddleware('marker'));

        // NOTICE ROUTES
        $group->get('/notices', function ($request, $response) use ($noticeService) {
            $controller = new NoticeController($noticeService);
            return $controller->findAll($request, $response);
        });

        $group->post('/notices', function ($request, $response) use ($noticeService) {
            $controller = new NoticeController($noticeService);
            return $controller->create($request, $response);
        })->add(new ValidationMiddleware('notice'));

        // ДИНАМИЧЕСКИЕ МАРШРУТЫ (С {id}) - В САМОМ КОНЦЕ
        $group->get('/editors/{id}', function ($request, $response, $args) use ($editorService) {
            $controller = new EditorController($editorService);
            return $controller->findById($request, $response, $args);
        });

        $group->put('/editors/{id}', function ($request, $response, $args) use ($editorService) {
            $controller = new EditorController($editorService);
            return $controller->update($request, $response, $args);
        })->add(new ValidationMiddleware('editor'));

        $group->delete('/editors/{id}', function ($request, $response, $args) use ($editorService) {
            $controller = new EditorController($editorService);
            return $controller->delete($request, $response, $args);
        });

        $group->get('/tweets/{id}', function ($request, $response, $args) use ($tweetService) {
            $controller = new TweetController($tweetService, null, null, null);
            return $controller->findById($request, $response, $args);
        });

        $group->put('/tweets/{id}', function ($request, $response, $args) use ($tweetService) {
            $controller = new TweetController($tweetService, null, null, null);
            return $controller->update($request, $response, $args);
        })->add(new ValidationMiddleware('tweet'));

        $group->delete('/tweets/{id}', function ($request, $response, $args) use ($tweetService) {
            $controller = new TweetController($tweetService, null, null, null);
            return $controller->delete($request, $response, $args);
        });

        $group->get('/markers/{id}', function ($request, $response, $args) use ($markerService) {
            $controller = new MarkerController($markerService);
            return $controller->findById($request, $response, $args);
        });

        $group->put('/markers/{id}', function ($request, $response, $args) use ($markerService) {
            $controller = new MarkerController($markerService);
            return $controller->update($request, $response, $args);
        })->add(new ValidationMiddleware('marker'));

        $group->delete('/markers/{id}', function ($request, $response, $args) use ($markerService) {
            $controller = new MarkerController($markerService);
            return $controller->delete($request, $response, $args);
        });

        $group->get('/notices/{id}', function ($request, $response, $args) use ($noticeService) {
            $controller = new NoticeController($noticeService);
            return $controller->findById($request, $response, $args);
        });

        $group->put('/notices/{id}', function ($request, $response, $args) use ($noticeService) {
            $controller = new NoticeController($noticeService);
            return $controller->update($request, $response, $args);
        })->add(new ValidationMiddleware('notice'));

        $group->delete('/notices/{id}', function ($request, $response, $args) use ($noticeService) {
            $controller = new NoticeController($noticeService);
            return $controller->delete($request, $response, $args);
        });
    });
};