#include <drogon/HttpAppFramework.h>
#include <controllers/TestCtrl.h>

int main() 
{
    drogon::app().loadConfigFile("/home/dmitry/Distcomp/351004/Lazuta/config.json");
    drogon::app().run();
    return 0;
}