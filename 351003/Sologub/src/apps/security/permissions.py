"""Role-based permission classes for the v2.0 API.

Policy
------
* ADMIN has unrestricted access.
* CUSTOMER has:
  - full access to their own Writer profile (retrieve/update/delete),
  - read access to everyone else's Writer data,
  - write access only to Stories / Notes they own (writerId == self.id),
  - read-only access everywhere else (e.g. Markers).
"""
from rest_framework import permissions

from apps.writers.models import Writer


SAFE_METHODS = permissions.SAFE_METHODS


def _is_admin(user) -> bool:
    return getattr(user, "is_authenticated", False) and getattr(user, "role", None) == Writer.ROLE_ADMIN


def _is_customer(user) -> bool:
    return getattr(user, "is_authenticated", False) and getattr(user, "role", None) == Writer.ROLE_CUSTOMER


class IsAuthenticatedJWT(permissions.BasePermission):
    message = "Authentication credentials were not provided."

    def has_permission(self, request, view):
        return bool(getattr(request.user, "is_authenticated", False))


class WriterPermission(IsAuthenticatedJWT):
    """Writers: admin full access; customer can only modify their own row."""

    def has_permission(self, request, view):
        if not super().has_permission(request, view):
            return False
        if _is_admin(request.user):
            return True
        if request.method in SAFE_METHODS:
            return True
        if view.action == "create":
            return False
        # For unsafe method on /writers/{pk} by a customer, reject up-front if
        # the target id differs from the caller's id. This ensures we return
        # 403 rather than leaking a 404 from get_object().
        pk = (view.kwargs or {}).get(getattr(view, "lookup_url_kwarg", None) or "pk")
        if pk is not None:
            try:
                target_id = int(pk)
            except (TypeError, ValueError):
                return False
            if target_id != int(request.user.id):
                return False
        return True

    def has_object_permission(self, request, view, obj):
        if _is_admin(request.user):
            return True
        if request.method in SAFE_METHODS:
            return True
        return obj.id == request.user.id


class OwnedResourcePermission(IsAuthenticatedJWT):
    """For Stories and Notes where objects carry a ``writerId``-like owner."""

    owner_field = "writerId_id"  # django FK column

    def has_permission(self, request, view):
        if not super().has_permission(request, view):
            return False
        if _is_admin(request.user):
            return True
        if request.method in SAFE_METHODS:
            return True
        # For create we check payload vs current user
        if view.action == "create":
            payload_owner = request.data.get("writerId") if hasattr(request, "data") else None
            if payload_owner is None:
                return True  # fall through to serializer validation
            try:
                return int(payload_owner) == int(request.user.id)
            except (TypeError, ValueError):
                return False
        return True

    def has_object_permission(self, request, view, obj):
        if _is_admin(request.user):
            return True
        if request.method in SAFE_METHODS:
            return True
        owner = getattr(obj, self.owner_field, None)
        if owner is None:
            # Fallback for non-ORM objects (e.g. dict from notes cache).
            owner = obj.get("writerId") if isinstance(obj, dict) else None
        try:
            return int(owner) == int(request.user.id)
        except (TypeError, ValueError):
            return False


class ReadOnlyForCustomer(IsAuthenticatedJWT):
    """Customers have read-only access; admins do everything."""

    def has_permission(self, request, view):
        if not super().has_permission(request, view):
            return False
        if _is_admin(request.user):
            return True
        return request.method in SAFE_METHODS
