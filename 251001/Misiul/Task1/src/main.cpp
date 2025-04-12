#include "Creator.hpp"
#include "Message.hpp"
#include "Tag.hpp"
#include "Topic.hpp"

#include "Server.hpp"

#include "EntityHandler.hpp"

using BaseServer = Server<
    EntityHandler<Creator>,
    EntityHandler<Message>,
    EntityHandler<Tag>,
    EntityHandler<Topic>
>;

int main() {
    BaseServer server;
    server.start_server();
    return 0;
}
