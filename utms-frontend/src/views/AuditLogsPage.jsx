import React, { useEffect, useState } from 'react';
import AuditLogService from '../services/AuditLogService';

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

    if (loading) return <div className="p-4 text-white">Loading logs...</div>;
    if (error) return <div className="p-4 text-red-500">{error}</div>;

    return (
        <div className="container mx-auto p-4">
            <div className="flex justify-between items-center mb-4">
                <h1 className="text-2xl font-bold text-white">System Audit Logs</h1>
                <button
                    onClick={onBack}
                    className="bg-gray-600 hover:bg-gray-500 text-white px-4 py-2 rounded shadow transition"
                >
                    &larr; Back to Dashboard
                </button>
            </div>
            <div className="overflow-x-auto bg-gray-800 rounded-lg shadow">
                <table className="min-w-full text-left text-sm whitespace-nowrap">
                    <thead className="uppercase tracking-wider border-b-2 border-gray-700 bg-gray-900 text-gray-300">
                        <tr>
                            <th scope="col" className="px-6 py-4">ID</th>
                            <th scope="col" className="px-6 py-4">Timestamp</th>
                            <th scope="col" className="px-6 py-4">Actor</th>
                            <th scope="col" className="px-6 py-4">Action</th>
                            <th scope="col" className="px-6 py-4">Target App ID</th>
                            <th scope="col" className="px-6 py-4">Details</th>
                        </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-700 text-gray-300">
                        {logs.map((log) => (
                            <tr key={log.id} className="hover:bg-gray-700 transition-colors">
                                <td className="px-6 py-4">{log.id}</td>
                                <td className="px-6 py-4">{new Date(log.timestamp).toLocaleString()}</td>
                                <td className="px-6 py-4 font-medium text-blue-400">{log.actorUsername}</td>
                                <td className="px-6 py-4">
                                    <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full 
                    ${log.actionType === 'SUBMIT' ? 'bg-green-100 text-green-800' :
                                            log.actionType === 'FORWARD' ? 'bg-blue-100 text-blue-800' :
                                                log.actionType === 'RETURN' ? 'bg-red-100 text-red-800' :
                                                    log.actionType === 'APPROVE' ? 'bg-purple-100 text-purple-800' :
                                                        'bg-gray-100 text-gray-800'}`}>
                                        {log.actionType}
                                    </span>
                                </td>
                                <td className="px-6 py-4">{log.targetApplicationId}</td>
                                <td className="px-6 py-4 max-w-xs truncate" title={log.details}>
                                    {log.details}
                                </td>
                            </tr>
                        ))}
                        {logs.length === 0 && (
                            <tr>
                                <td colSpan="6" className="px-6 py-4 text-center">No logs found.</td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default AuditLogsPage;
