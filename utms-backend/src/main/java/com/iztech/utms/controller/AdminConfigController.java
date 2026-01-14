package com.iztech.utms.controller;

import com.iztech.utms.service.ConfigurationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/config")
@RequiredArgsConstructor
public class AdminConfigController {

    private final ConfigurationService configurationService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> getCurrentConfigs() {
        return ResponseEntity.ok(configurationService.getAllConfigs());
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateConfigs(@RequestBody Map<String, String> updates) {
        configurationService.updateConfigs(updates);
        return ResponseEntity.ok().build();
    }
}
