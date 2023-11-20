package com.authservice.service;

import com.authservice.exceptions.NameDuplicateException;
import com.authservice.model.AuthenticatedAccount;
import com.authservice.model.User;
import com.authservice.model.UserRole;
import com.authservice.repository.UserRepository;
import com.authservice.repository.UserRoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.authservice.model.RoleEnum.USER_ROLE;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
        final UserRepository userRepository,
        final UserRoleRepository userRoleRepository,
        final PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(final User user) {
        Optional.ofNullable(userRepository.fetchUserByLogin(user.getLogin()))
            .ifPresent(existingUser -> {
                throw new NameDuplicateException("Login '" + user.getLogin() + "' already in use");
            });

        final UserRole userRole = userRoleRepository.fetchRoleByName(USER_ROLE);

        final long createdUserId = userRepository
            .createUser(user
                .setPassword(passwordEncoder.encode(user.getPassword()))
                .setRole(userRole)
        );

        return userRepository.fetchUserById(createdUserId);
    }

    public User findByLogin(final String login) {
        return userRepository.fetchUserByLogin(login);
    }

    public boolean isValidPassword(final User authUser, final User existedUser) {
       return passwordEncoder.matches(authUser.getPassword(), existedUser.getPassword());
    }

    public User findById(final Long userId) {
        return userRepository.fetchUserById(userId);
    }

    public List<User> getAllUsers() {
        return userRepository.fetchAllUsers();
    }

    public void deleteUser(final Long userId) {
        userRepository.deleteUser(userId);
    }

    public User updateUserInfo(
        final String firstName,
        final String secondName,
        final String email,
        final AuthenticatedAccount principal
    ) {
        userRepository.updateUserInformation(
            firstName,
            secondName,
            email,
            principal.getUserId()
        );

        return userRepository.fetchUserById(principal.getUserId());
    }

    public User updateUserPrivileges(final Long userId, final boolean hasPrivileges) {
        userRepository.updateUserPrivileges(userId, hasPrivileges);

        return userRepository.fetchUserById(userId);
    }

}
