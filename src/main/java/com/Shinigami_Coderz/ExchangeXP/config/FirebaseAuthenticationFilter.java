package com.Shinigami_Coderz.ExchangeXP.config;

import com.Shinigami_Coderz.ExchangeXP.entity.User;
import com.Shinigami_Coderz.ExchangeXP.service.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Verifies Firebase ID token from Authorization header and sets Spring Security context.
 * Principal is set to app username (so controllers that call authentication.getName() keep working).
 */
@Slf4j
public class FirebaseAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTH_HEADER = "Authorization";

    @Autowired
    private UserService userService; // <- ensure this bean exists

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader(AUTH_HEADER);
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            String idToken = header.substring(7);
            try {
                FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
                String uid = decodedToken.getUid();
                String email = decodedToken.getEmail();

//                if (email == null || !isEmailVerified(decodedToken)) {
//                    log.warn("FirebaseAuthenticationFilter: Email missing or not verified (email={})", email);
//                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                    return;
//                }


                // Find application user by email
                User appUser = userService.findUserByEmail(email);

                if (appUser == null) {
                    // Option B: require client to token in your app first via /public/create-user
                    log.warn("FirebaseAuthenticationFilter: No app user found for email={} - token valid but app registration required", email);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }

                // Build authorities from app user's roles
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                if (appUser.getRoles() != null) {
                    authorities = appUser.getRoles().stream()
                            .filter(Objects::nonNull)
                            .map(String::trim)
                            .map(r -> "ROLE_" + r.toUpperCase())
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());
                } else {
                    authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                }

                // Set principal to app username (keeps controllers unchanged)
                String principal = appUser.getUsername();

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(principal, idToken, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);

                log.debug("FirebaseAuthenticationFilter: Authenticated principal={} email={} uid={}", principal, email, uid);

            } catch (FirebaseAuthException e) {
                log.warn("FirebaseAuthenticationFilter: Token verification failed: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            } catch (Exception e) {
                log.error("FirebaseAuthenticationFilter: Unexpected error while verifying token", e);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }


    // tolerant parser for email_verified claim
    private boolean isEmailVerified(com.google.firebase.auth.FirebaseToken token) {
        if (token == null) return false;
        Object claim = token.getClaims().get("email_verified");
        if (claim == null) return false;

        if (claim instanceof Boolean) return (Boolean) claim;

        String s = claim.toString().trim().toLowerCase();
        if (s.equals("true") || s.equals("1") || s.equals("yes")) return true;

        if (claim instanceof Number) {
            return ((Number) claim).longValue() != 0L;
        }
        return false;
    }

}



