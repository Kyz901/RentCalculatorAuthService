package com.authservice.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserRole {

    private Integer id;
    private String name;

}
