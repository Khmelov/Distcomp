from dataclasses import dataclass, field


@dataclass(kw_only=True)
class Marker:
    id: int = field(default=0, init=False)
    name: str