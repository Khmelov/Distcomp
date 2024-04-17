<?php
declare(strict_types = 1);

namespace App\Trait;

use App\Trait\Mapper;

trait JsonEndpointTrait 
{

    use Mapper;

    protected function cje(bool $status, array $messages = []): array {
        return array(
            'status' => $status,
        ) + $messages;
    }

    protected function cjeObj(bool $status, object $responseTo): array {
        return array(
            'status' => $status,
        ) + (array)$responseTo;
    }

    protected function constructJsonEndpoint(bool $status, object $entity): array {
        return array(
            'status' => $status,
        ) + (array) $this->MapEntityToResponseDTO($entity);
    }

    protected function constructArrayJsonEndpoint(array $entities) : array {

        $result = [];
        foreach($entities as $entity) {

            $result[] = $this->MapEntityToResponseDTO($entity);
        }
        return $result;
    }
    
}