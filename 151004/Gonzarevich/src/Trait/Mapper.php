<?php

namespace App\Trait;

trait Mapper {

    private static function MapRequestDTOToEntity(object $responseTo) : object {

        $entityClassName = str_replace("DTO", "Entity", strstr(get_class($responseTo), "RequestTo", true));
        $result = new $entityClassName();

        foreach(get_object_vars($responseTo) as $key => $value) {

            $method = 'set'.ucfirst($key);
            if (isset($value)) {

                $result->$method($value);
            }
        }
        return $result;
    }

    private static function MapEntityToResponseDTO(Object $entity) : object {
        
        $entityResponseToClassName = str_replace("Entity", "DTO", get_class($entity)."ResponseTo");
        return new $entityResponseToClassName($entity);
    }
}