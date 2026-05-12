PREFIX = "distcomp"


def editor(editor_id: int) -> str:
    return f"{PREFIX}:editor:{editor_id}"


def editors_all() -> str:
    return f"{PREFIX}:editors:all"


def tweet(tweet_id: int) -> str:
    return f"{PREFIX}:tweet:{tweet_id}"


def tweets_all() -> str:
    return f"{PREFIX}:tweets:all"


def marker(marker_id: int) -> str:
    return f"{PREFIX}:marker:{marker_id}"


def markers_all() -> str:
    return f"{PREFIX}:markers:all"


def post(post_id: int) -> str:
    return f"{PREFIX}:post:{post_id}"


def posts_all() -> str:
    return f"{PREFIX}:posts:all"
