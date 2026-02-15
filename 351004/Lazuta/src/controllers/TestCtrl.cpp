#include "TestCtrl.h"

void TestCtrl::asyncHandleHttpRequest(const HttpRequestPtr &req, std::function<void (const HttpResponsePtr &)> &&callback)
{
    auto resp = HttpResponse::newHttpResponse();
    resp->setStatusCode(k200OK);
    resp->setContentTypeCode(CT_TEXT_HTML);
    resp->setBody("Hello World!");
    callback(resp);
}