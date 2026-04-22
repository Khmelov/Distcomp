authors_db = {}
issues_db = {}
tags_db = {}
comments_db = {}

def init_db():
    from models import Author
    authors_db.clear()
    issues_db.clear()
    tags_db.clear()
    comments_db.clear()
    
    
    authors_db[1] = Author(
        id=1,
        login="iosnaapagonkaosla@gmail.com",
        password="GeneratedPassword123!",
        firstname="Никита",
        lastname="Мельников"
    )