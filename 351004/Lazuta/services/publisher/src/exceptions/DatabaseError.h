#pragma once

enum class DatabaseError
{
    None,
    NotFound,
    AlreadyExists,
    InvalidData,
    DatabaseError,
    Unknown
};

