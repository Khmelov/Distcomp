from enum import Enum


class TweetErrorMessage(str, Enum):
    NOT_FOUND = "Tweet not found"

class CreatorErrorMessage(str, Enum):
    NOT_FOUND = "Creator not found"

class PostErrorMessage(str, Enum):
    NOT_FOUND = "Post not found"

class MarkerErrorMessage(str, Enum):
    NOT_FOUND = "Marker not found"