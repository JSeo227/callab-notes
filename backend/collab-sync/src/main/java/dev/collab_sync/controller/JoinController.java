package dev.collab_sync.controller;

import dev.collab_sync.dto.JoinDto;
import dev.collab_sync.service.JoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class JoinController {

    private final JoinService joinService;

    @PostMapping("/join")
    public String join(JoinDto joinDto) {
        joinService.join(joinDto);
        return "success";
    }
}
