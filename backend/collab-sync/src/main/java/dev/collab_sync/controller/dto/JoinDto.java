package dev.collab_sync.controller.dto;

import lombok.Data;

@Data
public class JoinDto {
    private String email;
    private String password;
    private String username;
}
