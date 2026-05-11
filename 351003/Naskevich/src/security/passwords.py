import bcrypt

# bcrypt enforces 72 bytes of the password in UTF-8; truncate on byte boundary.
_MAX_BCRYPT_BYTES = 72


def _password_bytes(plain: str) -> bytes:
    data = plain.encode("utf-8")
    if len(data) > _MAX_BCRYPT_BYTES:
        return data[:_MAX_BCRYPT_BYTES]
    return data


def hash_password(plain: str) -> str:
    return bcrypt.hashpw(_password_bytes(plain), bcrypt.gensalt()).decode("ascii")


def verify_password(plain: str, hashed: str) -> bool:
    if not hashed.startswith("$2"):
        return plain == hashed
    return bcrypt.checkpw(_password_bytes(plain), hashed.encode("ascii"))
