import { apiFetch } from './api';

class AuditLogService {
    getAllAuditLogs() {
        return apiFetch('/admin/audit-logs');
    }
}

export default new AuditLogService();
