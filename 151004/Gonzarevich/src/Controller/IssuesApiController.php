<?php

namespace App\Controller;

use Symfony\Component\HttpFoundation\JsonResponse as Response;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpKernel\Attribute\MapRequestPayload;
use App\Trait\JsonEndpointTrait;
use App\Validation\Requirement as ParamRules;

use App\Entity\Issue;
use App\DTO\{IssueRequestTo, IssueResponseTo};
use App\Service\IssueRepository;

#[Route('/issues', name: 'issues_', options: ['expose' => true])]
class IssuesApiController extends AbstractController
{
    use JsonEndpointTrait;

    function __construct(
        private IssueRepository $issueRepository
    ) {}

    #[Route('', name: 'show_all', methods: 'GET')]
    public function show_all(): Response
    {

        $issues = $this->issueRepository->all();
        $response = array();
        foreach($issues as $issue) {
            $response[] = new IssueResponseTo($issue);
        }
        return new Response($response, Response::HTTP_OK);
    }

    #[Route('/{id}', name: 'show', methods: 'GET', requirements: ['id' => ParamRules::DIGITS])]
    public function show(int $id): Response 
    {
        
        $issue = $this->issueRepository->issue($id);

        $response = $this->cjeObj(
            status: true,
            responseTo: new IssueResponseTo($issue),
        );

        return new Response($response, Response::HTTP_OK);
       
    }

    #[Route('', name: 'store', methods: 'POST')]
    public function store(
        #[MapRequestPayload] IssueRequestTo $issueReview
    ): Response
    {
        $issue = new Issue();
        $issue->populate(array_values(get_object_vars($issueReview)));

        $issueResponseTo = $this->issueRepository->create($issue);
        $response = $this->cjeObj(
            status: true,
            responseTo: $issueResponseTo,
        );
        
        return new Response($response, Response::HTTP_CREATED);
    }

    #[Route('', name: 'update', methods: ['PUT', 'PATCH'])]
    public function update(
        #[MapRequestPayload] IssueRequestTo $issueReview
    ): Response 
    {

        $issue = $this->issueRepository->issue($issueReview?->id);

        $response = null;
        if($issue == null) 
        {

            $response = $this->cje(
                status: false,
            );
            $response = new Response($response, Response::HTTP_NOT_FOUND);
        }
        else
        {

            $issue->populate(array_values(get_object_vars($issueReview)));
            $this->issueRepository->update();

            $response = $this->cjeObj(
                status: true,
                responseTo: new IssueResponseTo($issue),
            );
            $response = new Response($response, Response::HTTP_OK);
        }
        return $response;
    }

    #[Route('/{id}', name: 'destroy', methods: 'DELETE', requirements: ['id' => ParamRules::DIGITS])]
    public function destroy(int $id): Response
    {

        $issue = $this->issueRepository->issue($id);
        $response = null;

        if($issue == null) 
        {
            $response = $this->cje(
                status: false,
            );
            $response = new Response($response, Response::HTTP_NOT_FOUND);
        }
        else
        {

            $this->issueRepository->delete($issue);

            $response = $this->cje(
                status: true,
            );
            $response = new Response($response, Response::HTTP_NO_CONTENT);
        }
        
        return $response;
    }

}
