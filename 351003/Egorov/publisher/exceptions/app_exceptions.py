from fastapi import HTTPException, status


class AppError(HTTPException):
    def __init__(self, status_code: int, error_code: int, message: str) -> None:
        super().__init__(
            status_code=status_code,
            detail={"errorMessage": message, "errorCode": error_code},
        )


class CreatorNotFoundError(AppError):
    def __init__(self, creator_id: int) -> None:
        super().__init__(status.HTTP_404_NOT_FOUND, 40401, f"Creator {creator_id} not found")


class StoryNotFoundError(AppError):
    def __init__(self, story_id: int) -> None:
        super().__init__(status.HTTP_404_NOT_FOUND, 40402, f"Story {story_id} not found")


class MarkerNotFoundError(AppError):
    def __init__(self, marker_id: int) -> None:
        super().__init__(status.HTTP_404_NOT_FOUND, 40403, f"Marker {marker_id} not found")


class NoticeNotFoundError(AppError):
    def __init__(self, notice_id: int) -> None:
        super().__init__(status.HTTP_404_NOT_FOUND, 40404, f"Notice {notice_id} not found")

