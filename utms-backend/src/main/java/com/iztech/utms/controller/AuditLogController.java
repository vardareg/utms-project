package com.iztech.utms.controller;

import com.iztech.utms.model.AuditLog;
import com.iztech.utms.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogRepository auditLogRepository;

    @GetMapping
    public java.util.List<AuditLog> getAllAuditLogs() {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return auditLogRepository.findAll(Sort.by(Sort.Direction.DESC, "timestamp"));
        } else {
            // Filter for OIDB / DEAN / YGK: Only show App Status changes
            java.util.List<com.iztech.utms.model.ActionType> visibleActions = java.util.Arrays.asList(
                    com.iztech.utms.model.ActionType.SUBMIT,
                    com.iztech.utms.model.ActionType.FORWARD,
                    com.iztech.utms.model.ActionType.RETURN,
                    com.iztech.utms.model.ActionType.EVALUATE,
                    com.iztech.utms.model.ActionType.APPROVE,
                    com.iztech.utms.model.ActionType.ARCHIVE);
            return auditLogRepository.findByActionTypeIn(visibleActions, Sort.by(Sort.Direction.DESC, "timestamp"));
        }
    }
}
