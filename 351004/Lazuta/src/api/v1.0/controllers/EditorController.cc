#include "EditorController.h"

EditorController::EditorController(std::unique_ptr<EditorService> service)
{
    m_service = std::move(service);
}

void EditorController::CreateEditor(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback)
{
    HttpResponsePtr httpResponse;

    try
    {           
        auto jsonFromRequest = req->getJsonObject();
        EditorResponseTo dto = m_service->Create(EditorRequestTo::fromJson(jsonFromRequest.get()));
        auto jsonResponse = dto.toJson();      
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setBody(jsonResponse.asString());
        httpResponse->setStatusCode(HttpStatusCode::k201Created);
    }
    catch(const NotFoundException& e)
    {
        httpResponse->setStatusCode(HttpStatusCode::k404NotFound);
    }
    catch(const DatabaseException& e)
    {
        httpResponse->setStatusCode(HttpStatusCode::k500InternalServerError);
    }
    catch(const std::exception& e)
    {
        std::stringstream body;
        body << "Unknow error has occured at the server: " << e.what();
        httpResponse->setBody(body.str());
        httpResponse->setStatusCode(HttpStatusCode::k500InternalServerError);    
    }

    callback(httpResponse);
}

void EditorController::ReadEditor(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t id)
{
    HttpResponsePtr httpResponse;

    try
    {           
        auto jsonFromRequest = req->getJsonObject();
        EditorResponseTo dto = m_service->Read(id);
        auto jsonResponse = dto.toJson();      
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setBody(jsonResponse.asString());
        httpResponse->setStatusCode(HttpStatusCode::k200OK);
    }
    catch(const NotFoundException& e)
    {
        httpResponse->setStatusCode(HttpStatusCode::k404NotFound);
    }
    catch(const DatabaseException& e)
    {
        httpResponse->setStatusCode(HttpStatusCode::k500InternalServerError);
    }
    catch(const std::exception& e)
    {
        std::stringstream body;
        body << "Unknow error has occured at the server: " << e.what();
        httpResponse->setBody(body.str());
        httpResponse->setStatusCode(HttpStatusCode::k500InternalServerError);    
    }
    
    callback(httpResponse);
}

void EditorController::UpdateEditor(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback)
{
    HttpResponsePtr httpResponse;

    try
    {           
        auto jsonFromRequest = req->getJsonObject();
        auto to = EditorRequestTo::fromJson(jsonFromRequest.get());

        EditorResponseTo dto = m_service->Update(to, to.id.value());
        auto jsonResponse = dto.toJson();      
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setBody(jsonResponse.asString());
        httpResponse->setStatusCode(HttpStatusCode::k200OK);
    }
    catch(const NotFoundException& e)
    {
        httpResponse->setStatusCode(HttpStatusCode::k404NotFound);
    }
    catch(const DatabaseException& e)
    {
        httpResponse->setStatusCode(HttpStatusCode::k500InternalServerError);
    }
    catch(const std::exception& e)
    {
        std::stringstream body;
        body << "Unknow error has occured at the server: " << e.what();
        httpResponse->setBody(body.str());
        httpResponse->setStatusCode(HttpStatusCode::k500InternalServerError);    
    }
    
    callback(httpResponse);
}

void EditorController::DeleteEditor(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t id)
{
    HttpResponsePtr httpResponse;

    try
    {           
        auto jsonFromRequest = req->getJsonObject();

        if (m_service->Delete(id))    
            httpResponse->setStatusCode(HttpStatusCode::k200OK);
        else
            httpResponse->setStatusCode(HttpStatusCode::k404NotFound);
    }
    catch(const NotFoundException& e)
    {
        httpResponse->setStatusCode(HttpStatusCode::k404NotFound);
    }
    catch(const DatabaseException& e)
    {
        httpResponse->setStatusCode(HttpStatusCode::k500InternalServerError);
    }
    catch(const std::exception& e)
    {
        std::stringstream body;
        body << "Unknow error has occured at the server: " << e.what();
        httpResponse->setBody(body.str());
        httpResponse->setStatusCode(HttpStatusCode::k500InternalServerError);    
    }
    
    callback(httpResponse);
}

void EditorController::GetAllEditors(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback)
{
HttpResponsePtr httpResponse;

    try
    {           
        std::vector<EditorResponseTo> dtos = m_service->GetAll();
        Json::Value jsonResponse;  
        for (auto& dto: dtos)
            jsonResponse.append(dto.toJson());
            
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);

        httpResponse->setBody(jsonResponse.asString());
        httpResponse->setStatusCode(HttpStatusCode::k200OK);
    }
    catch(const NotFoundException& e)
    {
        httpResponse->setStatusCode(HttpStatusCode::k404NotFound);
    }
    catch(const DatabaseException& e)
    {
        httpResponse->setStatusCode(HttpStatusCode::k500InternalServerError);
    }
    catch(const std::exception& e)
    {
        std::stringstream body;
        body << "Unknow error has occured at the server: " << e.what();
        httpResponse->setBody(body.str());
        httpResponse->setStatusCode(HttpStatusCode::k500InternalServerError);    
    }
    
    callback(httpResponse);
}
