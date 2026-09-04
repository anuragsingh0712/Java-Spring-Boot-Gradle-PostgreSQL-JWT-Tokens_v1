package com.example.app.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

  private SecurityUtils() {}

  public static UserPrincipal getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal)) {
      return null;
    }
    return (UserPrincipal) authentication.getPrincipal();
  }

  public static Long getCurrentUserId() {
    UserPrincipal principal = getCurrentUser();
    return principal == null ? null : principal.getId();
  }

  public static String getCurrentRole() {
    UserPrincipal principal = getCurrentUser();
    return principal == null ? null : principal.getRole();
  }

  public static boolean isStaff() {
    String role = getCurrentRole();
    return role != null
        && (role.equals("SUPER_ADMIN")
            || role.equals("GYM_ADMIN")
            || role.equals("BRANCH_MANAGER")
            || role.equals("RECEPTIONIST")
            || role.equals("TRAINER"));
  }

  public static boolean isAdmin() {
    String role = getCurrentRole();
    return role != null
        && (role.equals("SUPER_ADMIN")
            || role.equals("GYM_ADMIN")
            || role.equals("BRANCH_MANAGER"));
  }
}
