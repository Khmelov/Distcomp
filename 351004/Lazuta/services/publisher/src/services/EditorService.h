#pragma once

#include <memory>
#include <vector>

#include <models/TblEditor.h>
#include <dto/requests/EditorRequestTo.h>
#include <dto/responses/EditorResponseTo.h>

namespace publisher
{

class EditorRepository;

class EditorService 
{
private:
    std::shared_ptr<EditorRepository> m_dao;
    
public:
    explicit EditorService(std::shared_ptr<EditorRepository> storage);
    
    dto::EditorResponseTo Create(const dto::EditorRequestTo& request);
    dto::EditorResponseTo Read(int64_t id);
    dto::EditorResponseTo Update(const dto::EditorRequestTo& request, int64_t id);
    bool Delete(int64_t id);
    std::vector<dto::EditorResponseTo> GetAll();
};

}