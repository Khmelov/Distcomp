from .errors import BadRequestError


def validate_length(field_name: str, value: str, min_len: int, max_len: int, suffix: int):
    if value is None:
        raise BadRequestError(f"{field_name} must not be null", suffix)

    stripped = value.strip()
    if not (min_len <= len(stripped) <= max_len):
        raise BadRequestError(
            f"{field_name} length must be between {min_len} and {max_len}",
            suffix
        )
    return stripped


def validate_positive_id(field_name: str, value: int, suffix: int):
    if value is None or value <= 0:
        raise BadRequestError(f"{field_name} must be positive", suffix)
    return value