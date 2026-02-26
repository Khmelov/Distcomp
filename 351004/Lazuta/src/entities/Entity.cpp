#include "Entity.h"

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

unsigned long Entity::GetID() const
{
    return m_id;
}
