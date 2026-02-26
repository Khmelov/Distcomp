#pragma once

#include "Entity.h"
#include <string>

class Label : public Entity
{
private:
    std::string m_name;

public:
    Label();
    Label(const std::string& name);
    
    ~Label();
    
    std::string getName() const;
    
    void setName(const std::string& name);
};