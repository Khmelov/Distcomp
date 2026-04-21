from pymongo import MongoClient

MONGO_URL = "mongodb://localhost:27017"
DB_NAME = "distcomp"

_db = None

def init_db():
    global _db
    client = MongoClient(MONGO_URL)
    _db = client[DB_NAME]

def get_collection():
    return _db["tbl_comment"]