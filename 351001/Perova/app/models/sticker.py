from dataclasses import dataclass

from app.models.base import BaseEntity


@dataclass
class Sticker(BaseEntity):
    name: str = ""
