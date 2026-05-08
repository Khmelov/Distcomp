from dataclasses import dataclass


@dataclass
class Marker:
    id: int | None = None
    name: str = ""

