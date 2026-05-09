class EntityNotFoundException(Exception):
    def __init__(self, entity_name: str, entity_id: int) -> None:
        self.entity_name = entity_name
        self.entity_id = entity_id
        super().__init__(f"{entity_name} with id={entity_id} not found")


class EntityAlreadyExistsException(Exception):
    def __init__(self, entity_name: str, field: str, value: str) -> None:
        self.entity_name = entity_name
        self.field = field
        self.value = value
        super().__init__(f"{entity_name} with {field}='{value}' already exists")
