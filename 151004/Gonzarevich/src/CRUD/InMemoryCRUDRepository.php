<?php

namespace App\CRUD;

class InMemoryCRUDRepository implements CRUDRepositoryInterface
{
    private $entities;
    private int $lastId;
    private string $storageName;
    
    function __construct(string $storage_name = "default.db")
    {

        $this->entities = new \SplObjectStorage();
        $this->storageName = $storage_name;
    }

    public function connect(string $class) {

        $name = stripslashes($class);
        $this->storageName = dirname($_SERVER['DOCUMENT_ROOT']).DIRECTORY_SEPARATOR."InMemory$name.db";
        $this->__init();
    }

    function __destruct()
    {

        self::flush();
    }

    protected function __init() 
    {
        $serialized = file_get_contents($this->storageName);
        if ($serialized == false) {

            $this->entities = new \SplObjectStorage();
        } else {

            $this->entities->unserialize($serialized);
        }
        $this->lastId = $this->entities->count();
    }
    
    public function create(object $entity): void
    {
        $entity->setId($this->lastId);
        $this->lastId += 1;
        $this->entities->attach($entity);
    }
    
    public function update(object $entity): void
    {
        //unnecessary
    }
    
    public function delete(object $entity): void
    {
        $this->entities->detach($entity);
    }
    
    public function findById(int $id): ?object
    {
        foreach ($this->entities as $entity) {
            if ($entity->getId() === $id) {
                return $entity;
            }
        }
        
        return null;
    }
    
    public function findAll(): array
    {
        $allEntities = [];
        
        foreach ($this->entities as $entity) {
            $allEntities[] = $entity;
        }
        
        return $allEntities;
    }

    public function flush() {

        $serialized = $this->entities->serialize();
        file_put_contents($this->storageName, $serialized);
    }

}