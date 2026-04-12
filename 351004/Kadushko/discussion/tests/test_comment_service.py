from unittest.mock import patch, MagicMock
import pytest
from app.schemas.comment import CommentCreate, CommentUpdate, CommentResponse


def test_comment_create_valid():
    c = CommentCreate(**{"issueId": 1, "content": "hello world"})
    assert c.issue_id == 1
    assert c.content == "hello world"

def test_comment_create_content_too_short():
    with pytest.raises(Exception):
        CommentCreate(**{"issueId": 1, "content": "x"})

def test_comment_response_alias():
    r = CommentResponse(id=1, issue_id=2, content="test")
    d = r.model_dump(by_alias=True)
    assert "issueId" in d
    assert d["issueId"] == 2


@patch("app.services.comment_service.get_collection")
def test_get_all(mock_col):
    mock_col.return_value.find.return_value = [
        {"id": 1, "issue_id": 10, "content": "test"}
    ]
    import app.services.comment_service as svc
    results = svc.get_all()
    assert len(results) == 1
    assert results[0].id == 1


@patch("app.services.comment_service.get_collection")
def test_get_by_id_found(mock_col):
    mock_col.return_value.find_one.return_value = {"id": 5, "issue_id": 10, "content": "hello"}
    import app.services.comment_service as svc
    result = svc.get_by_id(5)
    assert result is not None
    assert result.id == 5


@patch("app.services.comment_service.get_collection")
def test_get_by_id_not_found(mock_col):
    mock_col.return_value.find_one.return_value = None
    import app.services.comment_service as svc
    result = svc.get_by_id(999)
    assert result is None


@patch("app.services.comment_service._next_id", return_value=42)
@patch("app.services.comment_service.get_collection")
def test_create(mock_col, mock_id):
    mock_col.return_value.insert_one.return_value = MagicMock()
    import app.services.comment_service as svc
    data = CommentCreate(**{"issueId": 1, "content": "new comment"})
    result = svc.create(data)
    assert result.id == 42
    assert result.issue_id == 1


@patch("app.services.comment_service.get_collection")
def test_delete_not_found(mock_col):
    mock_col.return_value.delete_one.return_value = MagicMock(deleted_count=0)
    import app.services.comment_service as svc
    assert svc.delete(999) is False


@patch("app.services.comment_service.get_collection")
def test_delete_found(mock_col):
    mock_col.return_value.delete_one.return_value = MagicMock(deleted_count=1)
    import app.services.comment_service as svc
    assert svc.delete(7) is True