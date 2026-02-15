#pragma once

#include <drogon/HttpSimpleController.h>

using namespace drogon;

class TestCtrl: public drogon::HttpSimpleController<TestCtrl>
{
public:
    virtual void asyncHandleHttpRequest(const HttpRequestPtr &req, std::function<void (const HttpResponsePtr &)> &&callback) override;
    
    PATH_LIST_BEGIN
        PATH_ADD("/",Get,Post);
        PATH_ADD("/test",Get);
    PATH_LIST_END
};