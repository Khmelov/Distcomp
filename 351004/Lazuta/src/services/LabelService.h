#pragma once

#include <memory>
#include <vector>

#include <dto/responses/LabelResponseTo.h>
#include <dto/requests/LabelRequestTo.h>

namespace myapp
{

class LabelRepository;

class LabelService 
{
private:
    std::shared_ptr<LabelRepository> m_dao;
    
public:
    explicit LabelService(std::shared_ptr<LabelRepository> storage);
    
    dto::LabelResponseTo Create(const dto::LabelRequestTo& request);
    dto::LabelResponseTo Read(int64_t id);
    dto::LabelResponseTo Update(const dto::LabelRequestTo& request, int64_t id);
    bool Delete(int64_t id);
    std::vector<dto::LabelResponseTo> GetAll();
};

}