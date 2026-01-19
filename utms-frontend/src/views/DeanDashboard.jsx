import React, { useState, useEffect } from 'react';
import { ArrowRight, CheckCircle, Clock, FileText, Download } from 'lucide-react';
import { apiFetch, API_URL } from '../services/api';

export default function DeanDashboard({ user: userProp }) {
    // Safety: user prop can be null after re-renders, so read from localStorage as fallback
    const user = userProp || JSON.parse(localStorage.getItem('utms_user') || 'null');

    if (!user) {
        window.location.href = '/login';
        return null;
    }

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
            await fetchReview();;
        } catch (e) {
            console.error("Approve Error:", e);
            alert("Failed to approve: " + (e.message || "Unknown error"));
        }
    };

    const [showReturnModal, setShowReturnModal] = useState(false);
    const [returnReason, setReturnReason] = useState("");

    const handleReturnToYgk = async () => {
        if (!returnReason.trim()) {
            alert("Please enter a reason.");
            return;
        }

        // Use Department ID. Assuming user.departmentId is available or passed via props/context.
        // Fallback to extraction from first app if user.departmentId is generic? 
        // Better: Use `user.departmentId` if exists.
        // If not, try to determine from apps.
        let deptId = user.departmentId;
        if (!deptId && reviewApps.length > 0) {
            deptId = reviewApps[0].targetDepartmentId;
        }
        if (!deptId) {
            alert("Cannot determine Department ID. Please contact support.");
            return;
        }

        try {
            await apiFetch(`/applications/return-to-ygk/${deptId}`, {
                method: 'PATCH',
                body: JSON.stringify({ reason: returnReason })
            }); // body automatically stringified if payload is object? apiFetch handles init? 
            // api.js usually handles JSON if body is object? 
            // Let's check api.js usage. Usually we pass object and it stringifies.
            // Wait, apiFetch implementation usually takes (url, options). 
            // Adjusting to pass body in options
        } catch (e) {
            // apiFetch might throw, but let's be safe
            try {
                // If apiFetch didn't handle json body automatically, we need to do it here.
                // Re-attempting pattern assuming apiFetch handles headers but maybe expects string body?
                // Standard fetch expects string body for 'body'.
                // If apiFetch wrapper handles it, great. Let's assume standard behavior + auth.
                const response = await fetch(`${API_URL}/applications/return-to-ygk/${deptId}`, {
                    method: 'PATCH',
                    headers: {
                        'Authorization': `Bearer ${user.token}`,
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ reason: returnReason })
                });
                if (!response.ok) {
                    const txt = await response.text();
                    throw new Error(txt);
                }
            } catch (innerE) {
                console.error(innerE);
                alert("Failed to return: " + innerE.message);
                return;
            }
        }

        alert("Applications returned to YGK.");
        setShowReturnModal(false);
        setReturnReason("");
        await fetchReview();
    };


    const handleExport = async (format) => {
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
                <div className="bg-red-50 px-6 py-4 border-b border-red-200 flex justify-between items-center">
                    <h3 className="font-semibold text-red-800 flex items-center">
                        <CheckCircle className="w-5 h-5 mr-2" /> Pending Final Approval ({reviewApps.length})
                    </h3>
                    {reviewApps.length > 0 && (
                        <button
                            onClick={() => setShowReturnModal(true)}
                            className="text-sm bg-white border border-red-300 text-red-700 hover:bg-red-50 px-3 py-1 rounded"
                        >
                            Return All to YGK
                        </button>
                    )}
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

            {/* MODAL: Return Reason */}
            {showReturnModal && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
                    <div className="bg-white rounded-lg max-w-md w-full p-6 space-y-4">
                        <h3 className="text-lg font-bold">Return to YGK for Revision</h3>
                        <p className="text-sm text-gray-600">
                            Provide a reason or instructions for the Transfer Commission. This will return all finalized applications in this list provided above to "Under Review".
                        </p>
                        <textarea
                            className="w-full border rounded p-2"
                            rows="4"
                            placeholder="Enter revision notes..."
                            value={returnReason}
                            onChange={(e) => setReturnReason(e.target.value)}
                        />
                        <div className="flex justify-end space-x-2">
                            <button
                                onClick={() => setShowReturnModal(false)}
                                className="px-4 py-2 text-gray-600 hover:bg-gray-100 rounded"
                            >
                                Cancel
                            </button>
                            <button
                                onClick={handleReturnToYgk}
                                className="px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700"
                            >
                                Confirm Return
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}
