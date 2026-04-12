from src.dto.post import PostResponseTo
from src.messaging.post_messages import PostPayload
from src.models.post import Post


def post_to_payload(p: Post) -> PostPayload:
    return PostPayload(id=p.id, tweet_id=p.tweet_id, content=p.content, state=p.state)


def post_to_response(p: Post) -> PostResponseTo:
    return PostResponseTo.model_validate(p)
