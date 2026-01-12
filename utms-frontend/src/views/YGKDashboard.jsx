import React, { useState, useEffect } from 'react';
import { ClipboardCheck, ListOrdered, FileText, Download, CheckCircle, X } from 'lucide-react';
import { apiFetch, API_URL } from '../services/api';

export default function YGKDashboard({ user }) {
    const [viewMode, setViewMode] = useState('list'); // 'list', 'ranking'
    const [applications, setApplications] = useState([]);
    const [rankingData, setRankingData] = useState(null);
    const [selectedApp, setSelectedApp] = useState(null);
    const [loading, setLoading] = useState(false);

    // In a real app, YGK Member would be linked to a Dept ID. We mock Dept ID = 1 (Computer Eng) for demo.
    const DEPARTMENT_ID = 1;

    // Fetch Forwarded Applications
    const fetchApplications = async () => {
        setLoading(true);
        try {
            // Fetch FORWARDED applications for YGK to review
            const data = await apiFetch('/applications/status/FORWARDED');
            // Filter by Dept in frontend for demo (Backend should handle this filter via Department Repo)
            if (Array.isArray(data)) {
                setApplications(data.filter(app => app.departmentName === "Computer Engineering"));
            } else {
                setApplications([]);
            }
        } catch (error) {
            console.error("Failed to fetch applications", error);
            setApplications([]);
        } finally {
            setLoading(false);
        }
    };

    // Fetch Ranking
    const fetchRanking = async () => {
        setLoading(true);
        try {
            const data = await apiFetch(`/evaluations/ranking/${DEPARTMENT_ID}`);
            setRankingData(data);
        } catch (error) {
            console.error("Failed to fetch ranking", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (viewMode === 'list') fetchApplications();
        if (viewMode === 'ranking') fetchRanking();
    }, [viewMode]);

    // ------------------------------------------
    // ACTION HANDLERS
    // ------------------------------------------
    const handleDownload = async (docId, fileName) => {
        try {
            const response = await fetch(`${API_URL}/documents/download/${docId}`, {
                headers: { 'Authorization': `Bearer ${user.token}` }
            });
            if (!response.ok) throw new Error("Download failed");
            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = fileName;
            document.body.appendChild(a);
            a.click();
            a.remove();
        } catch (error) {
            alert("Error: " + error.message);
        }
    };

    const submitEvaluation = async (isEligible, note) => {
        try {
            await apiFetch(`/evaluations/${selectedApp.trackingId}`, {
                method: 'POST',
                body: JSON.stringify({ isEligible, note })
            });

            alert(`Evaluation Saved: ${isEligible ? 'Eligible' : 'Not Eligible'}`);
            setSelectedApp(null);
            fetchApplications();
        } catch (err) {
            alert(err.message);
        }
    };

    // ------------------------------------------
    // SUB-COMPONENT: EVALUATION MODAL
    // ------------------------------------------
    const EvaluationModal = ({ app, onClose }) => {
        const [note, setNote] = useState("");

        return (
            <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50">
                <div className="bg-white rounded-lg shadow-xl w-full max-w-3xl max-h-[90vh] overflow-y-auto">
                    <div className="bg-blue-900 text-white px-6 py-4 flex justify-between items-center">
                        <h3 className="font-bold text-lg">Evaluate Candidate #{app.trackingId}</h3>
                        <button onClick={onClose} className="hover:text-blue-200"><X size={20} /></button>
                    </div>

                    <div className="p-6 space-y-6">
                        {/* Summary */}
                        <div className="flex justify-between items-center bg-gray-50 p-4 rounded border">
                            <div>
                                <p className="text-gray-500 text-xs uppercase">Composite Score</p>
                                <p className="text-2xl font-bold text-blue-900">{app.compositeScore}</p>
                            </div>
                            <div className="text-right">
                                <p className="text-gray-500 text-xs uppercase">YKS / GPA</p>
                                <p className="font-mono font-medium">{app.yksScore} / {app.gpa}</p>
                            </div>
                        </div>

                        {/* Documents */}
                        <div>
                            <h4 className="font-bold text-gray-700 mb-2">Review Documents</h4>
                            <div className="space-y-2">
                                {app.documents && app.documents.map(doc => (
                                    <div key={doc.id} className="flex justify-between items-center bg-gray-50 p-2 rounded text-sm border">
                                        <span className="flex items-center"><FileText className="w-4 h-4 mr-2" /> {doc.type}</span>
                                        <button onClick={() => handleDownload(doc.id, doc.fileName)} className="text-blue-600 hover:underline flex items-center">
                                            <Download className="w-3 h-3 mr-1" /> Download
                                        </button>
                                    </div>
                                ))}
                            </div>
                        </div>

                        {/* Decision Form */}
                        <div className="bg-blue-50 p-4 rounded border border-blue-200">
                            <label className="block text-sm font-bold text-blue-900 mb-2">Evaluation Note (Internal)</label>
                            <textarea
                                className="w-full border rounded p-2 text-sm mb-4"
                                rows="3"
                                placeholder="e.g. Approved. Transcript verified."
                                value={note}
                                onChange={(e) => setNote(e.target.value)}
                            ></textarea>

                            <div className="flex justify-end space-x-3">
                                <button
                                    onClick={() => submitEvaluation(false, note)}
                                    className="px-4 py-2 border border-red-500 text-red-700 rounded hover:bg-red-50"
                                >
                                    Not Eligible
                                </button>
                                <button
                                    onClick={() => submitEvaluation(true, note)}
                                    className="px-4 py-2 bg-blue-700 text-white rounded hover:bg-blue-600 flex items-center"
                                >
                                    <CheckCircle className="w-4 h-4 mr-2" /> Confirm Eligibility
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        );
    };

    return (
        <div className="space-y-6">
            <div className="flex justify-between items-center">
                <h2 className="text-2xl font-bold text-gray-800">Transfer Commission (YGK)</h2>
                <div className="flex space-x-2 bg-white rounded shadow p-1">
                    <button
                        onClick={() => setViewMode('list')}
                        className={`px-4 py-2 rounded text-sm font-medium transition ${viewMode === 'list' ? 'bg-blue-100 text-blue-900' : 'text-gray-600 hover:bg-gray-50'}`}
                    >
                        <ClipboardCheck className="w-4 h-4 inline mr-2" /> Evaluations
                    </button>
                    <button
                        onClick={() => setViewMode('ranking')}
                        className={`px-4 py-2 rounded text-sm font-medium transition ${viewMode === 'ranking' ? 'bg-blue-100 text-blue-900' : 'text-gray-600 hover:bg-gray-50'}`}
                    >
                        <ListOrdered className="w-4 h-4 inline mr-2" /> Ranking List
                    </button>
                </div>
            </div>

            {/* MODE: EVALUATION LIST */}
            {viewMode === 'list' && (
                <div className="bg-white rounded-lg shadow overflow-hidden">
                    <div className="bg-gray-50 px-6 py-4 border-b border-gray-200">
                        <h3 className="font-semibold text-gray-700">Pending Evaluations</h3>
                    </div>
                    <div className="p-0">
                        {loading ? <div className="p-8 text-center text-gray-500">Loading...</div> :
                            applications.length === 0 ? <div className="p-8 text-center text-gray-500">No applications pending evaluation.</div> : (
                                <table className="w-full text-left">
                                    <thead className="bg-gray-50 text-gray-600 text-sm">
                                        <tr>
                                            <th className="px-6 py-3">ID</th>
                                            <th className="px-6 py-3">Student</th>
                                            <th className="px-6 py-3">Score</th>
                                            <th className="px-6 py-3">Status</th>
                                            <th className="px-6 py-3">Action</th>
                                        </tr>
                                    </thead>
                                    <tbody className="divide-y divide-gray-200">
                                        {applications.map((app) => (
                                            <tr key={app.trackingId} className="hover:bg-gray-50">
                                                <td className="px-6 py-4 font-mono text-sm">#{app.trackingId}</td>
                                                <td className="px-6 py-4">{app.studentName}</td>
                                                <td className="px-6 py-4 font-bold">{app.compositeScore}</td>
                                                <td className="px-6 py-4"><span className="bg-yellow-100 text-yellow-800 px-2 py-1 rounded text-xs">{app.status}</span></td>
                                                <td className="px-6 py-4">
                                                    <button onClick={() => setSelectedApp(app)} className="text-blue-600 hover:underline font-medium">Evaluate</button>
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            )}
                    </div>
                </div>
            )}

            {/* MODE: RANKING TABLE */}
            {viewMode === 'ranking' && rankingData && (
                <div className="space-y-6">
                    {/* PRIMARY LIST */}
                    <div className="bg-white rounded-lg shadow overflow-hidden border-l-4 border-green-500">
                        <div className="bg-green-50 px-6 py-4 border-b border-green-100 flex justify-between">
                            <h3 className="font-bold text-green-900">ASIL LISTE (Primary Candidates)</h3>
                            <span className="text-green-700 text-sm">Quota: {rankingData.quota}</span>
                        </div>
                        <table className="w-full text-left">
                            <thead className="text-xs uppercase text-gray-500 bg-gray-50">
                                <tr>
                                    <th className="px-6 py-2">Rank</th>
                                    <th className="px-6 py-2">Student</th>
                                    <th className="px-6 py-2">Composite Score</th>
                                </tr>
                            </thead>
                            <tbody>
                                {rankingData.primaryList.map((row) => (
                                    <tr key={row.trackingId} className="border-b">
                                        <td className="px-6 py-3 font-bold text-green-700">#{row.rank}</td>
                                        <td className="px-6 py-3">{row.studentName}</td>
                                        <td className="px-6 py-3 font-mono font-bold">{row.compositeScore}</td>
                                    </tr>
                                ))}
                                {rankingData.primaryList.length === 0 && (
                                    <tr><td colSpan="3" className="p-4 text-center text-gray-500">No eligible candidates yet.</td></tr>
                                )}
                            </tbody>
                        </table>
                    </div>

                    {/* WAITLIST */}
                    <div className="bg-white rounded-lg shadow overflow-hidden border-l-4 border-yellow-500">
                        <div className="bg-yellow-50 px-6 py-4 border-b border-yellow-100">
                            <h3 className="font-bold text-yellow-900">YEDEK LISTE (Waitlist)</h3>
                        </div>
                        <table className="w-full text-left">
                            <thead className="text-xs uppercase text-gray-500 bg-gray-50">
                                <tr>
                                    <th className="px-6 py-2">Rank</th>
                                    <th className="px-6 py-2">Student</th>
                                    <th className="px-6 py-2">Composite Score</th>
                                </tr>
                            </thead>
                            <tbody>
                                {rankingData.waitList.map((row) => (
                                    <tr key={row.trackingId} className="border-b">
                                        <td className="px-6 py-3 font-bold text-yellow-700">#{row.rank}</td>
                                        <td className="px-6 py-3">{row.studentName}</td>
                                        <td className="px-6 py-3 font-mono font-bold">{row.compositeScore}</td>
                                    </tr>
                                ))}
                                {rankingData.waitList.length === 0 && (
                                    <tr><td colSpan="3" className="p-4 text-center text-gray-500">Waitlist is empty.</td></tr>
                                )}
                            </tbody>
                        </table>
                    </div>
                </div>
            )}

            {selectedApp && <EvaluationModal app={selectedApp} onClose={() => setSelectedApp(null)} />}
        </div>
    );
}
