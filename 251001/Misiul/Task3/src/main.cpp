#include "PublisherServer.hpp"
#include "DiscussionServer.hpp"
#include "Entities/Creator.hpp"
#include "Entities/Message.hpp"
#include "Entities/Tag.hpp"
#include "Entities/Topic.hpp"

#include <thread>

int main() {
    PublisherServer<Creator, Topic, Tag> publisher;
    DiscussionServer<Message> discussion;

    auto discussion_thread = std::jthread([&](){
        discussion.start_server();
    });
    
    publisher.redirect("messages", 24130);
    publisher.start_server();
    return 0;
}
