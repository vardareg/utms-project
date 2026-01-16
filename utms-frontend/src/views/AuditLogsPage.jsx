import React, { useEffect, useState } from 'react';
import AuditLogService from '../services/AuditLogService';
import AuditLogsTable from '../components/AuditLogsTable';

const AuditLogsPage = ({ onBack }) => {
    const [logs, setLogs] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        fetchLogs();
    }, []);

    const fetchLogs = async () => {
        try {
            setLoading(true);
            const response = await AuditLogService.getAllAuditLogs();
            setLogs(response);
            setLoading(false);
        } catch (err) {
            console.error("Error fetching logs:", err);
            setError("Failed to fetch audit logs.");
            setLoading(false);
        }
    };

    if (loading) return <div className="p-8 text-gray-600">Loading logs...</div>;
    if (error) return <div className="p-8 text-red-600">{error}</div>;

    return (
        <div className="space-y-6">
            <div className="flex justify-between items-center">
                <h2 className="text-2xl font-bold text-gray-800">System Audit Logs</h2>
                <div className="flex space-x-2">
                    <button
                        onClick={fetchLogs}
                        className="text-sm text-blue-600 hover:underline px-3"
                    >
                        Refresh
                    </button>
                    <button
                        onClick={onBack}
                        className="bg-gray-600 hover:bg-gray-500 text-white px-4 py-2 rounded shadow transition text-sm"
                    >
                        &larr; Back to Dashboard
                    </button>
                </div>
            </div>

            <div className="bg-white rounded-lg shadow min-h-[500px] p-6">
                <AuditLogsTable logs={logs} />
            </div>
        </div>
    );
};

export default AuditLogsPage;
