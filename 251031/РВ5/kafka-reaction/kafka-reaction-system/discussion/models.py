from sqlalchemy import Column, Integer, String, ForeignKey, Enum
from sqlalchemy.orm import relationship
from sqlalchemy.ext.declarative import declarative_base

Base = declarative_base()

class Reaction(Base):
    __tablename__ = 'tbl_reactions'
    id = Column(Integer, primary_key=True)
    content = Column(String)
    state = Column(Enum('PENDING', 'APPROVE', 'DECLINE', name='reaction_state'))
    news_id = Column(Integer, ForeignKey('tbl_news.id'))