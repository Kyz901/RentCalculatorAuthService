package com.authservice.api;

import com.authservice.jwt.JwtTokenProvider;
import com.authservice.model.User;
import com.authservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthController {

    private final JwtTokenProvider tokenProvider;
    private final UserService userService;

    public AuthController(
        final JwtTokenProvider tokenProvider,
        final UserService userService
    ) {
        this.tokenProvider = tokenProvider;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity login(
        @RequestBody final User user
    ) {
        try {
            log.info("IN [POST login]: <start login> - initiated user: {}",
                user.getLogin()
            );

            final String userLogin = user.getLogin();
            final User existedUser = userService.findByLogin(userLogin);
            if(existedUser == null || !userService.isValidPassword(user, existedUser)) {
                throw new UsernameNotFoundException("User with username: " + userLogin + " does not exist");
            }
            final HashMap<String, String> response = new HashMap<>();
            response.put("token", tokenProvider.createToken(existedUser.getEmail()));

            log.info("IN [POST login]: <end login successfully> - authenticated user id: {} - initiated user: {}",
                existedUser.getId(),
                user.getLogin()
            );

            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    @PostMapping("/sign-up")
    public User createUser(
        @RequestBody final User user
    ) {
        log.info("IN [POST createUser]: <start registration> - initiated user: {}",
            user.getLogin()
        );

        return userService.register(user);
    }
}
