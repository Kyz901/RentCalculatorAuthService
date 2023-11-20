package com.authservice.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class User {

    private Long id;
    private String firstName;
    private String secondName;
    private String email;
    private String login;
    private String password;
    private UserRole role;
    private boolean hasPrivileges;
    private boolean isActive;
    private boolean isDeleted;

}
