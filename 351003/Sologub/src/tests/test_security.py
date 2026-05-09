"""Tests for the /api/v2.0 JWT-secured API.

The tests piggy-back on the Postgres testcontainer fixture already defined
in ``conftest.py`` so the behaviour matches what the running service does.
"""
import pytest
from rest_framework import status

from apps.security.passwords import hash_password, verify_password
from apps.security.jwt_utils import decode_token, generate_token
from apps.writers.models import Writer


REGISTER_URL = "/api/v2.0/writers"
LOGIN_URL = "/api/v2.0/login"
ME_URL = "/api/v2.0/me"


# --------------------------------------------------------------------------
# Unit-level tests (no DB required)
# --------------------------------------------------------------------------

def test_password_hash_roundtrip():
    hashed = hash_password("password123")
    assert hashed != "password123"
    assert verify_password("password123", hashed) is True
    assert verify_password("wrong", hashed) is False


def test_jwt_contains_required_claims():
    payload = generate_token(login="alice", role="ADMIN")
    token = payload["access_token"]
    claims = decode_token(token)
    for required in ("sub", "iat", "exp", "role"):
        assert required in claims, f"missing {required}"
    assert claims["sub"] == "alice"
    assert claims["role"] == "ADMIN"
    assert claims["exp"] > claims["iat"]


# --------------------------------------------------------------------------
# Integration tests (full request/response cycle)
# --------------------------------------------------------------------------

def _register(api_client, login="alice", password="password123", role="CUSTOMER"):
    return api_client.post(
        REGISTER_URL,
        {
            "login": login,
            "password": password,
            "firstname": "Alice",
            "lastname": "Adams",
            "role": role,
        },
        format="json",
    )


def _login(api_client, login, password):
    return api_client.post(
        LOGIN_URL,
        {"login": login, "password": password},
        format="json",
    )


def _auth(token):
    return {"HTTP_AUTHORIZATION": f"Bearer {token}"}


@pytest.mark.django_db
class TestRegistrationAndLogin:
    def test_register_customer_and_login(self, api_client):
        res = _register(api_client, login="bob", password="password123")
        assert res.status_code == status.HTTP_201_CREATED
        assert res.data["role"] == "CUSTOMER"
        # password must never be returned
        assert "password" not in res.data

        # password is stored hashed
        writer = Writer.objects.get(login="bob")
        assert writer.password != "password123"
        assert verify_password("password123", writer.password)

        login_res = _login(api_client, "bob", "password123")
        assert login_res.status_code == status.HTTP_200_OK
        assert "access_token" in login_res.data
        claims = decode_token(login_res.data["access_token"])
        assert claims["sub"] == "bob"
        assert claims["role"] == "CUSTOMER"

    def test_register_as_admin_is_allowed(self, api_client):
        res = _register(api_client, login="eve", role="ADMIN")
        assert res.status_code == status.HTTP_201_CREATED
        assert res.data["role"] == "ADMIN"

    def test_login_with_wrong_password_returns_401(self, api_client):
        _register(api_client, login="carol", password="password123")
        res = _login(api_client, "carol", "wrong-password")
        assert res.status_code == status.HTTP_401_UNAUTHORIZED
        assert str(res.data["errorCode"]).startswith("401")

    def test_duplicate_login_rejected(self, api_client):
        _register(api_client, login="dup")
        res = _register(api_client, login="dup")
        assert res.status_code == status.HTTP_400_BAD_REQUEST


@pytest.mark.django_db
class TestProtectedEndpoints:
    def test_stories_require_auth(self, api_client):
        res = api_client.get("/api/v2.0/stories")
        assert res.status_code in (
            status.HTTP_401_UNAUTHORIZED,
            status.HTTP_403_FORBIDDEN,
        )
        assert "errorCode" in res.data

    def test_invalid_jwt_rejected(self, api_client):
        res = api_client.get("/api/v2.0/stories", **_auth("not-a-valid-token"))
        assert res.status_code == status.HTTP_401_UNAUTHORIZED

    def test_me_returns_current_user(self, api_client):
        _register(api_client, login="meuser", password="password123")
        token = _login(api_client, "meuser", "password123").data["access_token"]
        res = api_client.get(ME_URL, **_auth(token))
        assert res.status_code == status.HTTP_200_OK
        assert res.data["login"] == "meuser"
        assert res.data["role"] == "CUSTOMER"


@pytest.mark.django_db
class TestRoleBasedAccess:
    def _make_admin(self, login="admin", password="password123"):
        writer = Writer.objects.create(
            login=login,
            password=hash_password(password),
            firstname="Ad",
            lastname="Min",
            role=Writer.ROLE_ADMIN,
        )
        return writer

    def test_customer_cannot_delete_other_writer(self, api_client):
        _register(api_client, login="cust1", password="password123")
        _register(api_client, login="cust2", password="password123")
        other = Writer.objects.get(login="cust2")
        token = _login(api_client, "cust1", "password123").data["access_token"]
        res = api_client.delete(f"/api/v2.0/writers/{other.id}", **_auth(token))
        assert res.status_code == status.HTTP_403_FORBIDDEN

    def test_customer_can_update_own_profile(self, api_client):
        _register(api_client, login="owner", password="password123")
        me = Writer.objects.get(login="owner")
        token = _login(api_client, "owner", "password123").data["access_token"]
        res = api_client.patch(
            f"/api/v2.0/writers/{me.id}",
            {"firstname": "Updated"},
            format="json",
            **_auth(token),
        )
        assert res.status_code == status.HTTP_200_OK
        assert res.data["firstname"] == "Updated"

    def test_admin_can_delete_any_writer(self, api_client):
        self._make_admin(login="root", password="password123")
        _register(api_client, login="victim", password="password123")
        victim = Writer.objects.get(login="victim")
        token = _login(api_client, "root", "password123").data["access_token"]
        res = api_client.delete(f"/api/v2.0/writers/{victim.id}", **_auth(token))
        assert res.status_code == status.HTTP_204_NO_CONTENT

    def test_customer_cannot_write_markers(self, api_client):
        _register(api_client, login="reader", password="password123")
        token = _login(api_client, "reader", "password123").data["access_token"]
        res = api_client.post(
            "/api/v2.0/markers",
            {"name": "blocked"},
            format="json",
            **_auth(token),
        )
        assert res.status_code == status.HTTP_403_FORBIDDEN

    def test_customer_can_read_markers(self, api_client):
        _register(api_client, login="reader2", password="password123")
        token = _login(api_client, "reader2", "password123").data["access_token"]
        res = api_client.get("/api/v2.0/markers", **_auth(token))
        assert res.status_code == status.HTTP_200_OK

    def test_customer_cannot_create_story_for_another_writer(self, api_client):
        _register(api_client, login="author", password="password123")
        _register(api_client, login="foreign", password="password123")
        foreign = Writer.objects.get(login="foreign")
        token = _login(api_client, "author", "password123").data["access_token"]
        res = api_client.post(
            "/api/v2.0/stories",
            {"writerId": foreign.id, "title": "Hijack", "content": "hello"},
            format="json",
            **_auth(token),
        )
        assert res.status_code == status.HTTP_403_FORBIDDEN


@pytest.mark.django_db
class TestErrorFormat:
    def test_error_body_shape(self, api_client):
        res = api_client.get("/api/v2.0/stories")
        assert "errorMessage" in res.data
        assert "errorCode" in res.data
        code = int(res.data["errorCode"])
        assert 10000 <= code <= 99999
        assert str(code)[:3] == str(res.status_code)
