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
    private final com.iztech.utms.repository.AdministrativeProfileRepository administrativeProfileRepository;
    private final com.iztech.utms.repository.UserRepository userRepository;

    @GetMapping
    public java.util.List<AuditLog> getAllAuditLogs() {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();

        String username = auth.getName();
        com.iztech.utms.model.User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isAdmin = user.getRole() == com.iztech.utms.model.User.Role.ROLE_ADMIN;
        boolean isDean = user.getRole() == com.iztech.utms.model.User.Role.ROLE_DEAN_OFFICE_STAFF;
        boolean isYgk = user.getRole() == com.iztech.utms.model.User.Role.ROLE_YGK;

        Sort sort = Sort.by(Sort.Direction.DESC, "timestamp");

        if (isAdmin) {
            return auditLogRepository.findAll(sort);
        }

        // Common visible actions for non-admins
        java.util.List<com.iztech.utms.model.ActionType> visibleActions = java.util.Arrays.asList(
                com.iztech.utms.model.ActionType.SUBMIT,
                com.iztech.utms.model.ActionType.FORWARD,
                com.iztech.utms.model.ActionType.RETURN,
                com.iztech.utms.model.ActionType.EVALUATE,
                com.iztech.utms.model.ActionType.APPROVE,
                com.iztech.utms.model.ActionType.ARCHIVE);

        if (isDean) {
            // Fetch Faculty ID from profile
            var profile = administrativeProfileRepository.findById(user.getId()).orElse(null);
            if (profile != null && profile.getFaculty() != null) {
                return auditLogRepository.findLogsByFaculty(profile.getFaculty().getId(), visibleActions, sort);
            }
            // Fallback if no profile
            return java.util.Collections.emptyList();
        } else if (isYgk) {
            // Fetch Department ID from profile
            var profile = administrativeProfileRepository.findById(user.getId()).orElse(null);
            if (profile != null && profile.getDepartment() != null) {
                return auditLogRepository.findLogsByDepartment(profile.getDepartment().getId(), visibleActions, sort);
            }
            // Fallback if no profile
            return java.util.Collections.emptyList();
        } else {
            // OIDB or others (fallback to existing logic or empty?)
            // Assuming OIDB sees all visible actions globally for now as per previous logic
            // which just checked !isAdmin
            return auditLogRepository.findByActionTypeIn(visibleActions, sort);
        }
    }
}
