from fastapi import HTTPException, status
def clear_storage(
        storage: dict
) -> dict:
    for key, _ in storage.items():
        
        if key != "id":
            storage[key] = ""
    storage["id"] = 10000000
    return storage
