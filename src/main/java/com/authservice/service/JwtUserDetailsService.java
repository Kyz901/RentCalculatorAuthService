package com.authservice.service;

import com.authservice.model.AuthenticatedAccount;
import com.authservice.model.User;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    private final UserService userService;

    public JwtUserDetailsService(final UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        final User user = userService.findByLogin(login);
        if(user == null) {
            throw new UsernameNotFoundException("User with login: " + login + " not found");
        }

        return AuthenticatedAccount
            .builder()
            .userId(user.getId())
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getSecondName())
            .hasPrivileges(user.isHasPrivileges())
            .accountNonLocked(user.isActive())
            .credentialsNonExpired(true)
            .authorities(AuthorityUtils.createAuthorityList(user.getRole().getName()))
            .build();
    }
}
