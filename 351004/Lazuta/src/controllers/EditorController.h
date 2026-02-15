#pragma once

#include <drogon/HttpController.h>

using namespace drogon;

class EditorController : public drogon::HttpController<EditorController>
{
  public:
    METHOD_LIST_BEGIN
    // use METHOD_ADD to add your custom processing function here;
    // METHOD_ADD(EditorController::get, "/{2}/{1}", Get); // path is /EditorController/{arg2}/{arg1}
    // METHOD_ADD(EditorController::your_method_name, "/{1}/{2}/list", Get); // path is /EditorController/{arg1}/{arg2}/list
    // ADD_METHOD_TO(EditorController::your_method_name, "/absolute/path/{1}/{2}/list", Get); // path is /absolute/path/{arg1}/{arg2}/list

    METHOD_LIST_END
    // your declaration of processing function maybe like this:
    // void get(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback, int p1, std::string p2);
    // void your_method_name(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback, double p1, int p2) const;
};
