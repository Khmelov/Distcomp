#include "IEntity.h"

Entity::Entity()
{
}

Entity::~Entity()
{
}

void Entity::SetId(unsigned long id)
{
    m_id = id;
}

unsigned long Entity::GetID()
{
    return m_id;
}
