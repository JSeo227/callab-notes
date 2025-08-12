package dev.collab_sync.dto;

import lombok.Data;

@Data
public class JoinDto {
    private String email;
    private String password;
    private String username;
}
