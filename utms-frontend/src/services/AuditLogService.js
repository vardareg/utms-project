import { apiFetch } from './api';

class AuditLogService {
    getAllAuditLogs() {
        return apiFetch('/audit-logs');
    }
}

export default new AuditLogService();
