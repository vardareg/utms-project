import React, { useState } from 'react';
import { FileText, Send, AlertTriangle, Eye, CheckCircle } from 'lucide-react';
import { apiFetch } from '../services/api';

const DEPARTMENTS = [
    { id: 1, name: "Computer Engineering" },
    { id: 2, name: "Mechanical Engineering" },
    { id: 3, name: "Architecture" }
];

export default function PublishResultsPanel() {
    const [selectedDept, setSelectedDept] = useState(DEPARTMENTS[0].id);
    const [rankingData, setRankingData] = useState(null);
    const [loading, setLoading] = useState(false);
    const [successMsg, setSuccessMsg] = useState(null);

    const handlePreview = async () => {
        setLoading(true);
        setRankingData(null);
        setSuccessMsg(null);
        try {
            const data = await apiFetch(`/evaluations/ranking/${selectedDept}`);
            setRankingData(data);
        } catch (error) {
            alert("Failed to load ranking: " + error.message);
        } finally {
            setLoading(false);
        }
    };

    const handlePublish = async () => {
        if (!window.confirm("ARE YOU SURE?\n\nThis will:\n1. Finalize application statuses (Approved/Waitlist).\n2. Generate and publish the official Result Announcement.\n3. Send notification emails to ALL candidates.\n\nThis action cannot be undone.")) {
            return;
        }

        setLoading(true);
        try {
            await apiFetch(`/announcements/publish-results/${selectedDept}`, { method: 'POST' });
            setSuccessMsg("Results have been published! Notifications were sent.");
            setRankingData(null); // Clear preview 
        } catch (error) {
            alert("Publish failed: " + error.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="bg-white rounded-lg shadow p-6">
            <h2 className="text-xl font-bold text-gray-800 mb-6 border-b pb-2 flex items-center">
                <Send className="w-5 h-5 mr-2 text-red-700" /> Publish Department Results
            </h2>

            {/* Selection */}
            <div className="flex items-end space-x-4 mb-8">
                <div className="w-64">
                    <label className="block text-sm font-medium text-gray-700 mb-1">Select Department</label>
                    <select
                        className="w-full border rounded px-3 py-2"
                        value={selectedDept}
                        onChange={(e) => setSelectedDept(parseInt(e.target.value))}
                    >
                        {DEPARTMENTS.map(d => (
                            <option key={d.id} value={d.id}>{d.name}</option>
                        ))}
                    </select>
                </div>
                <button
                    onClick={handlePreview}
                    disabled={loading}
                    className="px-4 py-2 bg-blue-100 text-blue-800 rounded hover:bg-blue-200 font-medium flex items-center"
                >
                    <Eye className="w-4 h-4 mr-2" /> Preview Ranking
                </button>
            </div>

            {/* Result Preview */}
            {rankingData && (
                <div className="animate-fade-in">
                    <div className="bg-yellow-50 border-l-4 border-yellow-400 p-4 mb-6">
                        <div className="flex">
                            <div className="flex-shrink-0">
                                <AlertTriangle className="h-5 w-5 text-yellow-400" aria-hidden="true" />
                            </div>
                            <div className="ml-3">
                                <p className="text-sm text-yellow-700 font-bold">
                                    Preview Mode
                                </p>
                                <p className="text-sm text-yellow-700">
                                    Review the candidates below. Clicking "Publish" will make this result official and notify students.
                                </p>
                            </div>
                        </div>
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
                        <div>
                            <h3 className="font-bold text-green-800 mb-2">Primary List (Asil) - Quota: {rankingData.quota}</h3>
                            <RankingTable list={rankingData.primaryList} type="PRIMARY" />
                        </div>
                        <div>
                            <h3 className="font-bold text-orange-800 mb-2">Waitlist (Yedek)</h3>
                            <RankingTable list={rankingData.waitList} type="WAITLIST" />
                        </div>
                    </div>

                    <div className="flex justify-end border-t pt-4">
                        <button
                            onClick={handlePublish}
                            disabled={loading}
                            className="px-6 py-3 bg-red-800 text-white rounded shadow hover:bg-red-900 font-bold text-lg flex items-center"
                        >
                            {loading ? "Processing..." : (
                                <>
                                    <Send className="w-5 h-5 mr-3" /> PUBLISH & NOTIFY
                                </>
                            )}
                        </button>
                    </div>
                </div>
            )}

            {successMsg && (
                <div className="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded relative flex items-center" role="alert">
                    <CheckCircle className="w-6 h-6 mr-3" />
                    <span className="block sm:inline">{successMsg}</span>
                </div>
            )}
        </div>
    );
}

const RankingTable = ({ list, type }) => {
    if (!list || list.length === 0) return <p className="text-gray-500 italic text-sm">No candidates.</p>;
    return (
        <table className="w-full text-sm border">
            <thead className="bg-gray-50">
                <tr>
                    <th className="px-3 py-2 border-b">#</th>
                    <th className="px-3 py-2 border-b">Tracking ID</th>
                    <th className="px-3 py-2 border-b">Candidate</th>
                    <th className="px-3 py-2 border-b">Score</th>
                </tr>
            </thead>
            <tbody>
                {list.map(item => (
                    <tr key={item.trackingId} className={type === 'PRIMARY' ? 'bg-green-50' : 'bg-orange-50'}>
                        <td className="px-3 py-2 border-b font-medium">{item.rank}</td>
                        <td className="px-3 py-2 border-b font-mono text-xs">#{item.trackingId}</td>
                        <td className="px-3 py-2 border-b">{item.studentName}</td>
                        <td className="px-3 py-2 border-b font-bold">{item.compositeScore}</td>
                    </tr>
                ))}
            </tbody>
        </table>
    );
};
