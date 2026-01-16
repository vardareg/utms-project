import React from 'react';
import { Activity, ShieldCheck, ShieldAlert } from 'lucide-react';

const AuditLogsTable = ({ logs }) => {
    return (
        <div className="overflow-x-auto">
            <table className="min-w-full leading-normal">
                <thead>
                    <tr>
                        <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                            Timestamp
                        </th>
                        <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                            Actor
                        </th>
                        <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                            Action
                        </th>
                        <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                            Details
                        </th>
                    </tr>
                </thead>
                <tbody>
                    {logs.map((log) => {
                        const isSuccess = log.actionType === 'LOGIN_SUCCESS';
                        const isFailure = log.actionType === 'LOGIN_FAILED';
                        let rowClass = "";
                        let icon = <Activity size={16} className="text-gray-500" />;

                        if (isSuccess) {
                            rowClass = "bg-green-50";
                            icon = <ShieldCheck size={16} className="text-green-600" />;
                        } else if (isFailure) {
                            rowClass = "bg-red-50";
                            icon = <ShieldAlert size={16} className="text-red-600" />;
                        }

                        return (
                            <tr key={log.id} className={rowClass}>
                                <td className="px-5 py-5 border-b border-gray-200 text-sm">
                                    <p className="text-gray-900 whitespace-no-wrap">
                                        {new Date(log.timestamp).toLocaleString()}
                                    </p>
                                </td>
                                <td className="px-5 py-5 border-b border-gray-200 text-sm">
                                    <p className="text-gray-900 whitespace-no-wrap font-medium">
                                        {log.actorUsername}
                                    </p>
                                </td>
                                <td className="px-5 py-5 border-b border-gray-200 text-sm">
                                    <div className="flex items-center gap-2">
                                        {icon}
                                        <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full 
                                            ${isSuccess ? 'bg-green-100 text-green-800' :
                                                isFailure ? 'bg-red-100 text-red-800' : 'bg-gray-100 text-gray-800'}`}>
                                            {log.actionType}
                                        </span>
                                    </div>
                                </td>
                                <td className="px-5 py-5 border-b border-gray-200 text-sm">
                                    <p className="text-gray-900 whitespace-no-wrap">
                                        {log.details}
                                    </p>
                                </td>
                            </tr>
                        );
                    })}
                    {logs.length === 0 && (
                        <tr>
                            <td colSpan="4" className="px-5 py-5 border-b border-gray-200 text-sm text-center text-gray-500">
                                No audit logs found.
                            </td>
                        </tr>
                    )}
                </tbody>
            </table>
        </div>
    );
};

export default AuditLogsTable;
