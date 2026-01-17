import React, { useState, useEffect } from 'react';
import { ArrowRight, CheckCircle, Clock, FileText, Download } from 'lucide-react';
import { apiFetch, API_URL } from '../services/api';

export default function DeanDashboard({ user }) {
    const [reviewApps, setReviewApps] = useState([]);
    const [monitoringApps, setMonitoringApps] = useState([]); // New state for monitoring
    const [loading, setLoading] = useState(false);

    const fetchReview = async () => {
        try {
            // Dean sees 'FINALIZED' applications (after YGK submitted) to Approve
            const finalizedData = await apiFetch('/applications/status/FINALIZED');
            setReviewApps(Array.isArray(finalizedData) ? finalizedData : []);

            // Monitoring: Fetch 'UNDER_REVIEW' for read-only view
            const underReviewData = await apiFetch('/applications/status/UNDER_REVIEW');
            setMonitoringApps(Array.isArray(underReviewData) ? underReviewData : []);
        } catch (e) {
            console.error(e);
        }
    };

    useEffect(() => {
        setLoading(true);
        fetchReview().finally(() => setLoading(false));
    }, []);

    const handleApprove = async (id) => {
        if (!window.confirm("Final Approve this application? Status will become APPROVED.")) return;
        try {
            await apiFetch(`/applications/${id}/approve`, { method: 'PATCH' });
            alert("Application Approved successfully!");
            await fetchReview(); // Await refresh to update UI immediately
        } catch (e) {
            console.error("Approve Error:", e);
            alert("Failed to approve: " + (e.message || "Unknown error"));
        }
    };

    const handleExport = async (format) => {
        // Use Department ID from User Profile (AdministrativeProfile)
        // If Faculty Dean (departmentId is null), we might need to export for a specific selected department.
        // For now, if Department Dean, use that ID. If Faculty Dean, we might default to 1 or handle selection later.
        // The user asked for "Dean for Comp Eng", so departmentId will be set.
        const deptId = user.departmentId || 1;
        try {
            const response = await fetch(`${API_URL}/evaluations/ranking/${deptId}/export?format=${format}`, {
                headers: { 'Authorization': `Bearer ${user.token}` }
            });
            if (!response.ok) throw new Error("Export failed");
            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `ranking_${deptId}.${format}`;
            document.body.appendChild(a);
            a.click();
            a.remove();
        } catch (error) {
            alert("Error: " + error.message);
        }
    };

    return (
        <div className="space-y-6">
            <div className="flex justify-between items-center">
                <h2 className="text-2xl font-bold text-gray-800">
                    {user.scopeName ? `${user.scopeName} Dean's Office Dashboard` : "Dean's Office Dashboard"}
                </h2>
                <div className="flex space-x-2">
                    <button onClick={() => handleExport('pdf')} className="text-red-700 hover:bg-red-50 px-3 py-2 rounded flex items-center border border-red-200 bg-white">
                        <FileText className="w-4 h-4 mr-2" /> Export Ranking PDF
                    </button>
                    <button onClick={() => handleExport('xlsx')} className="text-green-700 hover:bg-green-50 px-3 py-2 rounded flex items-center border border-green-200 bg-white">
                        <Download className="w-4 h-4 mr-2" /> Export Ranking Excel
                    </button>
                </div>
            </div>

            {/* SECTION 1: PENDING FINAL APPROVAL (Actionable) */}
            <div className="bg-white rounded-lg shadow overflow-hidden">
                <div className="bg-red-50 px-6 py-4 border-b border-red-200">
                    <h3 className="font-semibold text-red-800 flex items-center">
                        <CheckCircle className="w-5 h-5 mr-2" /> Pending Final Approval ({reviewApps.length})
                    </h3>
                </div>
                <div className="p-0">
                    {loading ? <div className="p-8 text-center text-gray-500">Loading...</div> : (
                        reviewApps.length === 0 ? <div className="p-8 text-center text-gray-500">No applications waiting for final approval.</div> : (
                            <table className="w-full text-left">
                                <thead className="bg-gray-50 text-gray-600 text-sm">
                                    <tr>
                                        <th className="px-6 py-3">ID</th>
                                        <th className="px-6 py-3">Student</th>
                                        <th className="px-6 py-3">Department</th>
                                        <th className="px-6 py-3">Score</th>
                                        <th className="px-6 py-3">Action</th>
                                    </tr>
                                </thead>
                                <tbody className="divide-y divide-gray-200">
                                    {reviewApps.map((app) => (
                                        <tr key={app.trackingId} className="hover:bg-gray-50">
                                            <td className="px-6 py-4 font-mono text-sm">#{app.trackingId}</td>
                                            <td className="px-6 py-4">{app.studentName}</td>
                                            <td className="px-6 py-4">{app.departmentName}</td>
                                            <td className="px-6 py-4 font-bold">{app.compositeScore}</td>
                                            <td className="px-6 py-4">
                                                <button
                                                    onClick={() => handleApprove(app.trackingId)}
                                                    className="bg-green-600 text-white px-3 py-1 rounded text-sm hover:bg-green-700"
                                                >
                                                    Approve
                                                </button>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        )
                    )}
                </div>
            </div>

            {/* SECTION 2: MONITORING (Read-Only) */}
            <div className="bg-white rounded-lg shadow overflow-hidden opacity-75">
                <div className="bg-gray-50 px-6 py-4 border-b border-gray-200">
                    <h3 className="font-semibold text-gray-800 flex items-center">
                        <Clock className="w-5 h-5 mr-2" /> Evaluations in Progress (YGK) ({monitoringApps.length})
                    </h3>
                </div>
                <div className="p-0">
                    {loading ? <div className="p-8 text-center text-gray-500">Loading...</div> : (
                        monitoringApps.length === 0 ? <div className="p-8 text-center text-gray-500">No applications currently under review by YGK.</div> : (
                            <table className="w-full text-left">
                                <thead className="bg-gray-50 text-gray-600 text-sm">
                                    <tr>
                                        <th className="px-6 py-3">ID</th>
                                        <th className="px-6 py-3">Student</th>
                                        <th className="px-6 py-3">Department</th>
                                        <th className="px-6 py-3">Status</th>
                                    </tr>
                                </thead>
                                <tbody className="divide-y divide-gray-200">
                                    {monitoringApps.map((app) => (
                                        <tr key={app.trackingId} className="hover:bg-gray-50">
                                            <td className="px-6 py-4 font-mono text-sm">#{app.trackingId}</td>
                                            <td className="px-6 py-4">{app.studentName}</td>
                                            <td className="px-6 py-4">{app.departmentName}</td>
                                            <td className="px-6 py-4">
                                                <span className="bg-yellow-100 text-yellow-800 px-2 py-1 rounded-full text-xs font-semibold">
                                                    Under Review
                                                </span>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        )
                    )}
                </div>
            </div>
        </div>
    );
}
