package com.iztech.utms.controller;

import com.iztech.utms.service.SystemMonitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/monitor")
@RequiredArgsConstructor
public class AdminMonitorController {

    private final SystemMonitorService systemMonitorService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getSystemStats() {
        return ResponseEntity.ok(systemMonitorService.getSystemStats());
    }
}
