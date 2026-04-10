<?php
namespace App\Repository;

abstract class InMemoryRepository implements RepositoryInterface {

    protected string $file;

    public function __construct() {
        $this->file = sys_get_temp_dir() . '/repo_' . str_replace('\\','_',static::class) . '.json';

        if (!file_exists($this->file)) {
            file_put_contents($this->file, json_encode([
                'nextId' => 1,
                'items' => []
            ]));
        }
    }

    private function read(): array {
        return json_decode(file_get_contents($this->file), true);
    }

    private function write(array $data): void {
        file_put_contents($this->file, json_encode($data));
    }

    public function save($entity) {
        $data = $this->read();
        $id = $data['nextId']++;
        $entity->setId($id);
        $data['items'][$id] = $entity->toArray();
        $this->write($data);
        return $entity;
    }

    abstract protected function createEntity(array $item);

    public function findById(int $id) {
        $data = $this->read();
        if (!isset($data['items'][$id])) return null;
        return $this->createEntity($data['items'][$id]);
    }

    public function findAll(): array {
        $data = $this->read();
        $result = [];
        foreach ($data['items'] as $item) {
            $result[] = $this->createEntity($item);
        }
        return $result;
    }

    public function update($entity) {
        $data = $this->read();
        $id = $entity->getId();
        if (!isset($data['items'][$id])) throw new \Exception("Entity not found");
        $data['items'][$id] = $entity->toArray();
        $this->write($data);
        return $entity;
    }

    public function delete(int $id): bool {
        $data = $this->read();
        if (!isset($data['items'][$id])) return false;
        unset($data['items'][$id]);
        $this->write($data);
        return true;
    }

    public function exists(int $id): bool {
        $data = $this->read();
        return isset($data['items'][$id]);
    }
}