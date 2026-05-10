from rest_framework.permissions import BasePermission, SAFE_METHODS


class IsAuthenticated(BasePermission):
    def has_permission(self, request, view):
        return (
            request.user is not None
            and hasattr(request.user, 'is_authenticated')
            and request.user.is_authenticated
        )


class IsAdmin(BasePermission):
    def has_permission(self, request, view):
        return (
            request.user is not None
            and hasattr(request.user, 'role')
            and request.user.role == 'ADMIN'
        )


class IsAdminOrReadOnly(BasePermission):
    """ADMIN can do anything; CUSTOMER can only read."""

    def has_permission(self, request, view):
        if not (request.user and hasattr(request.user, 'is_authenticated')
                and request.user.is_authenticated):
            return False
        if request.user.role == 'ADMIN':
            return True
        if request.method in SAFE_METHODS:
            return True
        return False


class IsAdminOrOwner(BasePermission):
    """
    ADMIN can do anything.
    CUSTOMER can read all, but only write/update/delete their own resources.
    """

    def has_permission(self, request, view):
        if not (request.user and hasattr(request.user, 'is_authenticated')
                and request.user.is_authenticated):
            return False
        return True

    def has_object_permission(self, request, view):
        if request.user.role == 'ADMIN':
            return True
        if request.method in SAFE_METHODS:
            return True
        return False


class EditorPermission(BasePermission):
    """
    ADMIN: full access.
    CUSTOMER: read all, but can only modify their own editor profile.
    """

    def has_permission(self, request, view):
        if not (request.user and hasattr(request.user, 'is_authenticated')
                and request.user.is_authenticated):
            return False
        return True

    def has_object_permission(self, request, view, obj):
        if request.user.role == 'ADMIN':
            return True
        if request.method in SAFE_METHODS:
            return True
        # CUSTOMER can only modify their own profile
        return obj.id == request.user.id


class IssuePermission(BasePermission):
    """
    ADMIN: full access.
    CUSTOMER: read all, write/update/delete only own issues.
    """

    def has_permission(self, request, view):
        if not (request.user and hasattr(request.user, 'is_authenticated')
                and request.user.is_authenticated):
            return False
        return True

    def has_object_permission(self, request, view, obj):
        if request.user.role == 'ADMIN':
            return True
        if request.method in SAFE_METHODS:
            return True
        return obj.editor_id == request.user.id


class MessagePermission(BasePermission):
    """
    ADMIN: full access.
    CUSTOMER: read all, write/update/delete only own messages (by issue owner).
    """

    def has_permission(self, request, view):
        if not (request.user and hasattr(request.user, 'is_authenticated')
                and request.user.is_authenticated):
            return False
        return True
