#pragma once

#include <vector>
#include <cstdint>

class Entity
{
private:
    unsigned long m_id;
public:
    Entity();
    virtual ~Entity();

    void SetId(unsigned long id);
    unsigned long GetID() const;

};
