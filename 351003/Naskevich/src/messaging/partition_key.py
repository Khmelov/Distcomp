from src.messaging.post_messages import PostCommandMessage


def partition_key_for_command(cmd: PostCommandMessage) -> bytes:
    if cmd.operation == "CREATE" and cmd.post is not None:
        return str(cmd.post.tweet_id).encode()
    if cmd.operation == "UPDATE" and cmd.tweet_id is not None:
        return str(cmd.tweet_id).encode()
    if cmd.operation == "GET_ALL":
        return b"__all_posts__"
    if cmd.post_id is not None:
        return str(cmd.post_id).encode()
    return b"__default__"
