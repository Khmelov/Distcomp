#include "Label.h"

Label::Label() : Entity()
{
    m_name = "";
}

Label::Label(const std::string& name)
    : Entity()
{
    m_name = name;
}

Label::~Label()
{
}

std::string Label::getName() const
{
    return m_name;
}

void Label::setName(const std::string& name)
{
    m_name = name;
}