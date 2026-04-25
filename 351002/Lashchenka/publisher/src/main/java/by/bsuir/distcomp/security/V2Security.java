package by.bsuir.distcomp.security;

import by.bsuir.distcomp.exception.ForbiddenException;
import by.bsuir.distcomp.exception.UnauthorizedException;
import by.bsuir.distcomp.model.EditorRole;
import org.springframework.security.core.context.SecurityContextHolder;

public final class V2Security {

    private V2Security() {}

    public static EditorAuthPrincipal currentEditor() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof EditorAuthPrincipal p)) {
            throw new UnauthorizedException("Authentication required", 40101);
        }
        return p;
    }

    public static void requireAdmin() {
        if (currentEditor().getRole() != EditorRole.ADMIN) {
            throw new ForbiddenException("Admin role required", 40301);
        }
    }

    public static void requireSelfOrAdmin(long editorId) {
        EditorAuthPrincipal p = currentEditor();
        if (p.getRole() == EditorRole.ADMIN) {
            return;
        }
        if (p.getEditorId() != editorId) {
            throw new ForbiddenException("Not allowed to modify this resource", 40302);
        }
    }

    public static boolean isAdmin(EditorAuthPrincipal p) {
        return p.getRole() == EditorRole.ADMIN;
    }
}
