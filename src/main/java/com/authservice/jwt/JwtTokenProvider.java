package com.authservice.jwt;

import com.authservice.exceptions.JwtAuthenticationException;
import com.authservice.model.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class JwtTokenProvider {

    private static final int BEARER_WORD_LENGTH = 7;

    @Value("${jwt.token.secret}")
    private String jwtSecret;
    @Value("${jwt.token.expired}")
    private Long expirationMs;

    @Autowired
    private UserDetailsService userDetailsService;

    @PostConstruct
    protected void init() {
        jwtSecret = Base64.getEncoder().encodeToString(jwtSecret.getBytes());
    }

    public String createToken(final String email) {
        final Date now = new Date();

        return Jwts.builder()
            .setClaims(Jwts.claims().setSubject(email))
            .setIssuedAt(now)
            .setExpiration(new Date(now.getTime() + expirationMs))
            .signWith(SignatureAlgorithm.HS256, jwtSecret)
            .compact();
    }

    public Authentication getAuthentication(final String token) {
        final UserDetails userDetails = userDetailsService.loadUserByUsername(getUsername(token));

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsername(final String token) {
        return Jwts.parser()
            .setSigningKey(jwtSecret)
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }

    public boolean validateToken(final String token) {
        try {
            final Jws<Claims> claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);

            return !claims
                .getBody()
                .getExpiration()
                .before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtAuthenticationException("JWT token is expired or invalid");
        }
    }

    public String resolveToken(final HttpServletRequest request) {
        final String bearer = request.getHeader("Authorization");
        if(bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(BEARER_WORD_LENGTH);
        }
        return null;
    }

    private List<String> getRoleNames(final List<UserRole> userRoles) {
        return userRoles
            .stream()
            .map(UserRole::getName)
            .collect(toList());
    }
}
