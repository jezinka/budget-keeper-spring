package com.example.budgetkeeperspring.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDto {
    private String name;
    private String email;

    public UserDto(String name, String email) {
        this.name = name;
        this.email = email;
    }

}
