using rest1.application.interfaces;
using rest1.core.entities;

namespace rest1.infrastructure.persistence;

public class NewsRepository: Repository<News>, INewsRepository
{
    
}