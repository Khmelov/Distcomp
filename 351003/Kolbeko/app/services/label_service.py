from app.repository.memory import label_repo
from app.schemas.label import LabelRequestTo, LabelResponseTo
from app.core.exceptions import AppException

class LabelService:
    def create(self, dto: LabelRequestTo) -> LabelResponseTo:
        return LabelResponseTo(**label_repo.save(dto.model_dump()))

    def get_all(self):
        return [LabelResponseTo(**l) for l in label_repo.find_all()]

    def get_by_id(self, id: int):
        res = label_repo.find_by_id(id)
        if not res: raise AppException(404, "Label not found", 9)
        return LabelResponseTo(**res)

    def update(self, id: int, dto: LabelRequestTo) -> LabelResponseTo:
        data = dto.model_dump()
        data["id"] = id
        return LabelResponseTo(**label_repo.save(data))

    def delete(self, id: int):
        if not label_repo.delete(id):
            raise AppException(404, "Label not found", 11)