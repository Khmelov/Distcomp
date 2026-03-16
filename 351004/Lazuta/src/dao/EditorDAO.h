#pragma once

#include "DAO.h"
#include <entities/Editor.h>

class EditorDAO : public DAO<Editor> 
{
public:
    virtual ~EditorDAO() = default;

    virtual std::optional<Editor> FindByLogin(const std::string& login) = 0;
    virtual bool ExistsByLogin(const std::string& login) = 0;
};