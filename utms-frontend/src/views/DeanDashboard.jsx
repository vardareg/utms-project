import React, { useState, useEffect } from 'react';
import { ArrowRight, CheckCircle, Clock } from 'lucide-react';
import { apiFetch } from '../services/api';

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
            fetchIncoming();
            fetchReview();
        } catch (e) {
            alert(e.message);
        }
    };

    const handleApprove = async (id) => {
        if (!window.confirm("Final Approve this application? Status will become APPROVED.")) return;
        try {
            await apiFetch(`/applications/${id}/approve`, { method: 'PATCH' });
            alert("Application Approved!");
            fetchReview();
        } catch (e) {
            alert(e.message);
        }
    };

    return (
        <div className="space-y-6">
            <h2 className="text-2xl font-bold text-gray-800">Dean's Office Dashboard</h2>

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
