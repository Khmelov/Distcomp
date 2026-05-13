import os

from fastapi.testclient import TestClient

os.environ.setdefault("JWT_SECRET_KEY", "test-secret-key")

from main import get_app  # noqa: E402


client = TestClient(get_app())


def test_register_login_and_access_protected_endpoint() -> None:
    register_response = client.post(
        "/api/v2.0/creators",
        json={
            "login": "auth_customer",
            "password": "StrongPass123",
            "firstName": "Auth",
            "lastName": "Customer",
            "role": "CUSTOMER",
        },
    )
    assert register_response.status_code == 201
    creator = register_response.json()
    assert "password" not in creator

    login_response = client.post(
        "/api/v2.0/login",
        json={"login": "auth_customer", "password": "StrongPass123"},
    )
    assert login_response.status_code == 200
    token = login_response.json()["access_token"]

    creators_response = client.get(
        "/api/v2.0/creators",
        headers={"Authorization": f"Bearer {token}"},
    )
    assert creators_response.status_code == 200
    assert any(item["login"] == "auth_customer" for item in creators_response.json())


def test_customer_cannot_create_marker() -> None:
    register_response = client.post(
        "/api/v2.0/creators",
        json={
            "login": "marker_customer",
            "password": "StrongPass123",
            "firstName": "Marker",
            "lastName": "Customer",
            "role": "CUSTOMER",
        },
    )
    assert register_response.status_code == 201

    login_response = client.post(
        "/api/v2.0/login",
        json={"login": "marker_customer", "password": "StrongPass123"},
    )
    assert login_response.status_code == 200
    token = login_response.json()["access_token"]

    marker_response = client.post(
        "/api/v2.0/markers",
        json={"name": "security"},
        headers={"Authorization": f"Bearer {token}"},
    )
    assert marker_response.status_code == 403
    assert marker_response.json()["errorCode"] == 40300
