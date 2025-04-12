#include "Entities/Creator.hpp"
#include "Entities/Message.hpp"
#include "Entities/Tag.hpp"
#include "Entities/Topic.hpp"

#include "Server.hpp"

#include "EntityHandler.hpp"

using BaseServer = Server<
    EntityHandler<Creator>,
    EntityHandler<Topic>,
    EntityHandler<Message>,
    EntityHandler<Tag>
>;

int main() {
    BaseServer server;
    server.start_server();
    return 0;
}
