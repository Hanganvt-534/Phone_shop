package com.project.phone_shop.Config;

import com.project.phone_shop.Entity.User;
import com.project.phone_shop.Repository.UserRepository;
import com.project.phone_shop.exception.AppException;
import com.project.phone_shop.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
public class SecurityUtils {

    private SecurityUtils() {
        // Private constructor for utility class
    }

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static Optional<String> getCurrentUsername() {
        Authentication authentication = getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return Optional.empty();
        }

        if (authentication.getPrincipal() instanceof Jwt jwt) {
            String username = jwt.getClaim("username");
            if (username != null && !username.isBlank()) {
                return Optional.of(username);
            }
            return Optional.ofNullable(jwt.getSubject());
        }

        return Optional.ofNullable(authentication.getName());
    }

    public static Optional<Long> getCurrentUserId() {
        Authentication authentication = getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return Optional.empty();
        }

        if (authentication.getPrincipal() instanceof Jwt jwt) {
            Object userIdObj = jwt.getClaim("user_id");
            if (userIdObj == null) {
                userIdObj = jwt.getClaim("userId");
            }
            if (userIdObj != null) {
                try {
                    return Optional.of(Long.valueOf(userIdObj.toString()));
                } catch (NumberFormatException ignored) {
                    log.warn("Cannot parse user_id claim: {}", userIdObj);
                }
            }

            // Fallback: if subject was numeric user ID
            try {
                return Optional.of(Long.valueOf(jwt.getSubject()));
            } catch (NumberFormatException ignored) {}
        }

        return Optional.empty();
    }

    public static User getCurrentUser(UserRepository userRepository) {
        Authentication authentication = getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        // Try getting by userId claim first
        Optional<Long> userIdOpt = getCurrentUserId();
        if (userIdOpt.isPresent()) {
            Optional<User> userById = userRepository.findById(userIdOpt.get());
            if (userById.isPresent()) {
                return userById.get();
            }
        }

        // Try getting by username
        Optional<String> usernameOpt = getCurrentUsername();
        if (usernameOpt.isPresent()) {
            return userRepository.findByUsername(usernameOpt.get())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        }

        throw new AppException(ErrorCode.UNAUTHENTICATED);
    }

    public static boolean isAdmin() {
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_ADMIN") || a.equals("ADMIN"));
    }

    public static List<String> getCurrentUserRoles() {
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            return Collections.emptyList();
        }
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
    }
}
