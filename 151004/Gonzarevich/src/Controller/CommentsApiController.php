<?php

namespace App\Controller;

use Symfony\Component\HttpFoundation\JsonResponse as Response;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpKernel\Attribute\MapRequestPayload;
use App\Trait\JsonEndpointTrait;
use App\Validation\Requirement as ParamRules;

use App\Entity\Comment;
use App\DTO\{CommentRequestTo, CommentResponseTo};
use App\Service\CommentRepository;

#[Route('/comments', name: 'comments_', options: ['expose' => true])]
class CommentsApiController extends AbstractController
{
    use JsonEndpointTrait;

    function __construct(
        private CommentRepository $commentRepository
    ) {}

    #[Route('', name: 'show_all', methods: 'GET')]
    public function show_all(): Response
    {

        $comments = $this->commentRepository->all();
        $response = array();
        foreach($comments as $comment) {
            $response[] = new CommentResponseTo($comment);
        }
        return new Response($response, Response::HTTP_OK);
    }

    #[Route('/{id}', name: 'show', methods: 'GET', requirements: ['id' => ParamRules::DIGITS])]
    public function show(int $id): Response 
    {
        
        $comment = $this->commentRepository->comment($id);

        $response = $this->cjeObj(
            status: true,
            responseTo: new CommentResponseTo($comment),
        );

        return new Response($response, Response::HTTP_OK);
       
    }

    #[Route('', name: 'store', methods: 'POST')]
    public function store(
        #[MapRequestPayload] CommentRequestTo $commentReview
    ): Response
    {
        $comment = new Comment();
        $comment->populate(array_values(get_object_vars($commentReview)));

        $commentResponseTo = $this->commentRepository->create($comment);
        $response = $this->cjeObj(
            status: true,
            responseTo: $commentResponseTo,
        );
        
        return new Response($response, Response::HTTP_CREATED);
    }

    #[Route('', name: 'update', methods: ['PUT', 'PATCH'])]
    public function update(
        #[MapRequestPayload] CommentRequestTo $commentReview
    ): Response 
    {

        $comment = $this->commentRepository->comment($commentReview?->id);

        $response = null;
        if($comment == null) 
        {

            $response = $this->cje(
                status: false,
            );
            $response = new Response($response, Response::HTTP_NOT_FOUND);
        }
        else
        {

            $comment->populate(array_values(get_object_vars($commentReview)));
            $this->commentRepository->update();

            $response = $this->cjeObj(
                status: true,
                responseTo: new CommentResponseTo($comment),
            );
            $response = new Response($response, Response::HTTP_OK);
        }
        return $response;
    }

    #[Route('/{id}', name: 'destroy', methods: 'DELETE', requirements: ['id' => ParamRules::DIGITS])]
    public function destroy(int $id): Response
    {

        $comment = $this->commentRepository->comment($id);
        $response = null;

        if($comment == null) 
        {
            $response = $this->cje(
                status: false,
            );
            $response = new Response($response, Response::HTTP_NOT_FOUND);
        }
        else
        {

            $this->commentRepository->delete($comment);

            $response = $this->cje(
                status: true,
            );
            $response = new Response($response, Response::HTTP_NO_CONTENT);
        }
        
        return $response;
    }

}
