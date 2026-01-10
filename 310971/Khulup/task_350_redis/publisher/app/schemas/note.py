from pydantic import BaseModel, Field, ConfigDict
from datetime import datetime
from typing import Optional

class NoteCreate(BaseModel):
    country: str = Field("US", description="Код страны")
    issueId: int = Field(..., description="ID связанного Issue")
    content: str = Field(..., min_length=2, max_length=2048, description="Содержание заметки")

class NoteRead(BaseModel):
    model_config = ConfigDict(from_attributes=True, populate_by_name=True)
    
    id: int = Field(..., description="ID заметки")
    issueId: int = Field(..., description="ID связанного Issue")
    content: str = Field(..., description="Содержание заметки")
    createdAt: datetime = Field(..., description="Время создания")
    updatedAt: Optional[datetime] = Field(None, description="Время обновления")

class NoteUpdate(BaseModel):
    id: int = Field(..., description="ID заметки")
    country: str = Field("US", description="Код страны")
    issueId: int = Field(..., description="ID связанного Issue")
    content: str = Field(..., min_length=2, max_length=2048, description="Содержание заметки")
