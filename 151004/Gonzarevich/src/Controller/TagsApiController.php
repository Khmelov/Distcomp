<?php

namespace App\Controller;

use Symfony\Component\HttpFoundation\JsonResponse as Response;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpKernel\Attribute\MapRequestPayload;
use App\Trait\JsonEndpointTrait;
use App\Validation\Requirement as ParamRules;

use App\Entity\Tag;
use App\DTO\{TagRequestTo, TagResponseTo};
use App\Service\TagRepository;

#[Route('/tags', name: 'tags_', options: ['expose' => true])]
class TagsApiController extends AbstractController
{
    use JsonEndpointTrait;

    function __construct(
        private TagRepository $tagRepository
    ) {}

    #[Route('', name: 'show_all', methods: 'GET')]
    public function show_all(): Response
    {

        $tags = $this->tagRepository->all();
        $response = array();
        foreach($tags as $tag) {
            $response[] = new TagResponseTo($tag);
        }
        return new Response($response, Response::HTTP_OK);
    }

    #[Route('/{id}', name: 'show', methods: 'GET', requirements: ['id' => ParamRules::DIGITS])]
    public function show(int $id): Response 
    {
        
        $tag = $this->tagRepository->tag($id);

        $response = $this->cjeObj(
            status: true,
            responseTo: new TagResponseTo($tag),
        );

        return new Response($response, Response::HTTP_OK);
       
    }

    #[Route('', name: 'store', methods: 'POST')]
    public function store(
        #[MapRequestPayload] TagRequestTo $tagReview
    ): Response
    {
        $tag = new Tag();
        $tag->populate(array_values(get_object_vars($tagReview)));

        $tagResponseTo = $this->tagRepository->create($tag);
        $response = $this->cjeObj(
            status: true,
            responseTo: $tagResponseTo,
        );
        
        return new Response($response, Response::HTTP_CREATED);
    }

    #[Route('', name: 'update', methods: ['PUT', 'PATCH'])]
    public function update(
        #[MapRequestPayload] TagRequestTo $tagReview
    ): Response 
    {

        $tag = $this->tagRepository->tag($tagReview?->id);

        $response = null;
        if($tag == null) 
        {

            $response = $this->cje(
                status: false,
            );
            $response = new Response($response, Response::HTTP_NOT_FOUND);
        }
        else
        {

            $tag->populate(array_values(get_object_vars($tagReview)));
            $this->tagRepository->update();

            $response = $this->cjeObj(
                status: true,
                responseTo: new TagResponseTo($tag),
            );
            $response = new Response($response, Response::HTTP_OK);
        }
        return $response;
    }

    #[Route('/{id}', name: 'destroy', methods: 'DELETE', requirements: ['id' => ParamRules::DIGITS])]
    public function destroy(int $id): Response
    {

        $tag = $this->tagRepository->tag($id);
        $response = null;

        if($tag == null) 
        {
            $response = $this->cje(
                status: false,
            );
            $response = new Response($response, Response::HTTP_NOT_FOUND);
        }
        else
        {

            $this->tagRepository->delete($tag);

            $response = $this->cje(
                status: true,
            );
            $response = new Response($response, Response::HTTP_NO_CONTENT);
        }
        
        return $response;
    }

}
