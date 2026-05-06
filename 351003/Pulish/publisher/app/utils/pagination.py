def paginate(items, page: int = 0, size: int = 10):
    start = page * size
    end = start + size
    return items[start:end]
