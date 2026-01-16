import React, { useState, useEffect } from 'react';
import { ArrowRight, CheckCircle, Clock, FileText, Download } from 'lucide-react';
import { apiFetch, API_URL } from '../services/api';

export default function DeanDashboard({ user }) {
    const [incomingApps, setIncomingApps] = useState([]);
    const [reviewApps, setReviewApps] = useState([]);
    const [loading, setLoading] = useState(false);
    const [activeTab, setActiveTab] = useState('INCOMING'); // INCOMING | APPROVAL

    const fetchIncoming = async () => {
        try {
            // Dean sees 'FORWARDED' apps to assign to YGK
            const data = await apiFetch('/applications/status/FORWARDED');
            setIncomingApps(Array.isArray(data) ? data : []);
        } catch (e) {
            console.error(e);
        }
    };

    const fetchReview = async () => {
        try {
            // Dean sees 'UNDER_REVIEW' or 'FINALIZED' to Approve
            // Since backend is simple, we fetch UNDER_REVIEW. 
            // Ideally YGK moves them to FINALIZED, but YGKDashboard just Evaluates (keeping UNDER_REVIEW or similar).
            // We'll fetch UNDER_REVIEW for waiting approval.
            const data = await apiFetch('/applications/status/UNDER_REVIEW');
            setReviewApps(Array.isArray(data) ? data : []);
        } catch (e) {
            console.error(e);
        }
    };

    useEffect(() => {
        setLoading(true);
        Promise.all([fetchIncoming(), fetchReview()]).finally(() => setLoading(false));
    }, []);

    const handleAssign = async (id) => {
        if (!window.confirm("Assign this application to YGK Commission?")) return;
        try {
            await apiFetch(`/applications/${id}/assign-ygk`, { method: 'PATCH' });
            alert("Assigned to YGK.");
            await fetchIncoming(); // Await refresh
            await fetchReview();   // Await refresh
        } catch (e) {
            console.error("Assign Error:", e);
            alert("Failed to assign: " + e.message);
        }
    };

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

            {/* TABS */}
            <div className="flex border-b border-gray-200">
                <button
                    onClick={() => setActiveTab('INCOMING')}
                    className={`px-6 py-3 font-medium transition ${activeTab === 'INCOMING' ? 'border-b-2 border-red-900 text-red-900' : 'text-gray-500 hover:text-gray-700'}`}
                >
                    Incoming ({incomingApps.length})
                </button>
                <button
                    onClick={() => setActiveTab('APPROVAL')}
                    className={`px-6 py-3 font-medium transition ${activeTab === 'APPROVAL' ? 'border-b-2 border-red-900 text-red-900' : 'text-gray-500 hover:text-gray-700'}`}
                >
                    Pending Approval ({reviewApps.length})
                </button>
            </div>

            {/* CONTENT */}
            <div className="bg-white rounded shadow p-6">
                {loading ? <p>Loading...</p> : (
                    <>
                        {activeTab === 'INCOMING' && (
                            <div>
                                <h3 className="font-bold mb-4 text-lg">Assign to Commission</h3>
                                {incomingApps.length === 0 ? <p className="text-gray-500">No new applications from OIDB.</p> : (
                                    <table className="w-full text-left">
                                        <thead className="bg-gray-50">
                                            <tr>
                                                <th className="p-3">ID</th>
                                                <th className="p-3">Student</th>
                                                <th className="p-3">Score</th>
                                                <th className="p-3">Action</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {incomingApps.map(app => (
                                                <tr key={app.trackingId} className="border-b">
                                                    <td className="p-3">#{app.trackingId}</td>
                                                    <td className="p-3">{app.studentName}</td>
                                                    <td className="p-3 font-mono">{app.compositeScore}</td>
                                                    <td className="p-3">
                                                        <button
                                                            onClick={() => handleAssign(app.trackingId)}
                                                            className="bg-blue-600 text-white px-3 py-1 rounded text-sm hover:bg-blue-700 flex items-center"
                                                        >
                                                            Asign to YGK <ArrowRight className="ml-1 w-3 h-3" />
                                                        </button>
                                                    </td>
                                                </tr>
                                            ))}
                                        </tbody>
                                    </table>
                                )}
                            </div>
                        )}

                        {activeTab === 'APPROVAL' && (
                            <div>
                                <h3 className="font-bold mb-4 text-lg">Final Approval</h3>
                                {reviewApps.length === 0 ? <p className="text-gray-500">No applications pending final approval.</p> : (
                                    <table className="w-full text-left">
                                        <thead className="bg-gray-50">
                                            <tr>
                                                <th className="p-3">ID</th>
                                                <th className="p-3">Student</th>
                                                <th className="p-3">Score</th>
                                                <th className="p-3">Action</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {reviewApps.map(app => (
                                                <tr key={app.trackingId} className="border-b">
                                                    <td className="p-3">#{app.trackingId}</td>
                                                    <td className="p-3">{app.studentName}</td>
                                                    <td className="p-3 font-mono">{app.compositeScore}</td>
                                                    <td className="p-3">
                                                        <button
                                                            onClick={() => handleApprove(app.trackingId)}
                                                            className="bg-green-600 text-white px-3 py-1 rounded text-sm hover:bg-green-700 flex items-center"
                                                        >
                                                            <CheckCircle className="mr-1 w-3 h-3" /> Approve
                                                        </button>
                                                    </td>
                                                </tr>
                                            ))}
                                        </tbody>
                                    </table>
                                )}
                            </div>
                        )}
                    </>
                )}
            </div>
        </div>
    );
}
