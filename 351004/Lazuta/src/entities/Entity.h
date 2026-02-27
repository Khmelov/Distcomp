#pragma once

#include <vector>
#include <cstdint>

namespace myapp::entities
{

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

};
