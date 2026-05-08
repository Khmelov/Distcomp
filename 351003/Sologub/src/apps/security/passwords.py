"""BCrypt password hashing utilities.

The Writer table stores passwords in the form produced by ``hash_password``.
If a plain-text legacy password is encountered (no BCrypt marker), it is
compared as-is so existing fixtures continue to work, but new writers are
always stored hashed.
"""
import bcrypt


_BCRYPT_PREFIXES = (b"$2a$", b"$2b$", b"$2y$")


def hash_password(raw: str) -> str:
    if raw is None:
        raise ValueError("password must not be None")
    salt = bcrypt.gensalt(rounds=12)
    hashed = bcrypt.hashpw(raw.encode("utf-8"), salt)
    return hashed.decode("utf-8")


def is_hashed(stored: str) -> bool:
    if not stored:
        return False
    return stored.encode("utf-8")[:4] in _BCRYPT_PREFIXES


def verify_password(raw: str, stored: str) -> bool:
    if raw is None or stored is None:
        return False
    if is_hashed(stored):
        try:
            return bcrypt.checkpw(raw.encode("utf-8"), stored.encode("utf-8"))
        except ValueError:
            return False
    return raw == stored
