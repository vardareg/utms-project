import React, { useState, useEffect } from 'react';
import { Eye, CheckCircle, XCircle, ArrowRight, Download, FileText, X, Bell } from 'lucide-react';
import { apiFetch, API_URL } from '../services/api';
import AdminAnnouncementPanel from '../components/AdminAnnouncementPanel';
import PublishResultsPanel from '../components/PublishResultsPanel';

export default function OIDBDashboard({ user: userProp }) {
    // Safety: user prop can be null after re-renders, so read from localStorage as fallback
    const user = userProp || JSON.parse(localStorage.getItem('utms_user') || 'null');

    if (!user) {
        window.location.href = '/login';
        return null;
    }

    const [applications, setApplications] = useState([]);
    const [selectedApp, setSelectedApp] = useState(null);
    const [loading, setLoading] = useState(false);
    const [viewMode, setViewMode] = useState('NEW'); // NEW | RESUBMITTED | ANNOUNCEMENTS

    const fetchApplications = async () => {
        if (viewMode === 'ANNOUNCEMENTS') return;
        setLoading(true);
        try {
            const data = await apiFetch(`/applications/status/${viewMode}`);
            setApplications(Array.isArray(data) ? data : []);
        } catch (error) {
            console.error("Failed to fetch applications", error);
            setApplications([]);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchApplications();
    }, [viewMode]);

    const handleForward = async (appId) => {
        try {
            await apiFetch(`/applications/${appId}/forward`, { method: 'PATCH' });
            alert("Application forwarded successfully. Notification email sent to student.");
            setSelectedApp(null);
            fetchApplications();
        } catch (err) {
            alert(`Error: ${err.message}`);
        }
    };

    const handleVerify = async (appId) => {
        try {
            await apiFetch(`/evaluations/verify/${appId}`, { method: 'POST' });
            alert("Verification completed. Check status.");
            // Re-fetch applications to update list (though detail view might need explicit update if open)
            // Ideally we re-fetch the specific app, but for now we simply refresh the list
            // However, since we are in a Modal, we should probably close it or verify updates.
            // Let's close it to be safe and force refresh.
            setSelectedApp(null);
            fetchApplications();
        } catch (err) {
            alert(`Error: ${err.message}`);
        }
    };

    const handleReturn = async (appId, reason) => {
        try {
            await apiFetch(`/applications/${appId}/return`, {
                method: 'PATCH',
                body: JSON.stringify({ reason })
            });
            alert("Application returned. Notification email sent to student.");
            setSelectedApp(null);
            fetchApplications();
        } catch (err) {
            alert(`Error: ${err.message}`);
        }
    };

    const handleDownload = async (docId, fileName) => {
        try {
            const userStr = localStorage.getItem('utms_user');
            const token = userStr ? JSON.parse(userStr).token : '';

            const response = await fetch(`${API_URL}/documents/download/${docId}`, {
                headers: { 'Authorization': `Bearer ${token}` }
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

    const ReturnModal = ({ app, onClose, onConfirm }) => {
        const [reason, setReason] = useState("");
        return (
            <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50">
                <div className="bg-white rounded-lg shadow-xl w-full max-w-md p-6">
                    <h3 className="text-lg font-bold text-red-900 mb-4">Return Application #{app.trackingId}</h3>
                    <p className="text-sm text-gray-600 mb-2">Please specify the reason for return (e.g., Missing Document).</p>
                    <textarea
                        className="w-full border rounded p-2 mb-4 h-24"
                        placeholder="Enter reason..."
                        value={reason}
                        onChange={(e) => setReason(e.target.value)}
                    ></textarea>
                    <div className="flex justify-end space-x-2">
                        <button onClick={onClose} className="px-4 py-2 text-gray-600 hover:bg-gray-100 rounded">Cancel</button>
                        <button
                            onClick={() => onConfirm(app.trackingId, reason)}
                            disabled={!reason.trim()}
                            className="px-4 py-2 bg-red-900 text-white rounded hover:bg-red-800 disabled:opacity-50"
                        >
                            Confirm Return
                        </button>
                    </div>
                </div>
            </div>
        );
    };

    const ConfirmationModal = ({ title, message, onClose, onConfirm }) => (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50">
            <div className="bg-white rounded-lg shadow-xl w-full max-w-sm p-6">
                <h3 className="text-lg font-bold text-gray-900 mb-2">{title}</h3>
                <p className="text-sm text-gray-600 mb-6">{message}</p>
                <div className="flex justify-end space-x-2">
                    <button onClick={onClose} className="px-4 py-2 text-gray-600 hover:bg-gray-100 rounded">Cancel</button>
                    <button
                        onClick={onConfirm}
                        className="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700"
                    >
                        Confirm
                    </button>
                </div>
            </div>
        </div>
    );

    const DetailModal = ({ app, onClose }) => {
        const [showReturn, setShowReturn] = useState(false);
        const [showForwardConfirm, setShowForwardConfirm] = useState(false);

        if (showReturn) return <ReturnModal app={app} onClose={() => setShowReturn(false)} onConfirm={handleReturn} />;
        if (showForwardConfirm) return (
            <ConfirmationModal
                title="Forward Application"
                message="Are you sure you want to forward this application to the Faculty?"
                onClose={() => setShowForwardConfirm(false)}
                onConfirm={() => {
                    handleForward(app.trackingId);
                    setShowForwardConfirm(false);
                }}
            />
        );

        return (
            <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50">
                <div className="bg-white rounded-lg shadow-xl w-full max-w-4xl max-h-[90vh] overflow-y-auto">
                    <div className="bg-blue-900 text-white px-6 py-4 flex justify-between items-center">
                        <h3 className="font-bold text-lg">Application Details #{app.trackingId}</h3>
                        <button onClick={onClose} className="hover:text-blue-200"><X size={20} /></button>
                    </div>
                    <div className="p-6 grid grid-cols-1 md:grid-cols-2 gap-6">
                        {/* Info */}
                        <div className="space-y-4">
                            <h4 className="font-bold text-gray-700 border-b pb-2">Applicant Info</h4>
                            <div className="grid grid-cols-2 gap-4 text-sm">
                                <div><span className="block text-gray-500">Student</span> <span className="font-medium">{app.studentName}</span></div>
                                <div><span className="block text-gray-500">Department</span> <span className="font-medium">{app.departmentName}</span></div>
                                <div><span className="block text-gray-500">YKS Score</span> <span className="font-medium">{app.yksScore}</span></div>
                                <div><span className="block text-gray-500">GPA</span> <span className="font-medium">{app.gpa}</span></div>

                                <div className="col-span-2 bg-blue-50 p-2 rounded">
                                    <span className="block text-xs uppercase text-blue-700">Calculated Score</span>
                                    <span className="font-mono text-xl font-bold text-blue-900">{app.compositeScore}</span>
                                </div>

                                <div className="col-span-2 bg-gray-50 p-2 rounded border flex justify-between items-center">
                                    <div>
                                        <span className="block text-xs uppercase text-gray-500">Data Verification</span>
                                        <span className={`font-bold text-sm ${app.dataVerificationStatus === 'VERIFIED' ? 'text-green-600' : 'text-red-600'}`}>
                                            {app.dataVerificationStatus || 'Pending'}
                                        </span>
                                    </div>
                                    {!app.dataVerificationStatus && (
                                        <button
                                            onClick={() => handleVerify(app.trackingId)}
                                            className="text-xs bg-blue-100 text-blue-800 px-2 py-1 rounded hover:bg-blue-200"
                                        >
                                            Verify with UBYS
                                        </button>
                                    )}
                                </div>
                            </div>
                        </div>

                        {/* Documents */}
                        <div>
                            <h4 className="font-bold text-gray-700 border-b pb-2 mb-4">Documents</h4>
                            {app.documents && app.documents.length > 0 ? (
                                <div className="space-y-2">
                                    {app.documents.map(doc => (
                                        <div key={doc.id} className="flex justify-between items-center bg-gray-50 p-3 rounded border">
                                            <div className="flex items-center">
                                                <FileText className="w-5 h-5 text-gray-500 mr-2" />
                                                <span className="text-sm font-medium">{doc.type}</span>
                                            </div>
                                            <button
                                                onClick={() => handleDownload(doc.id, doc.fileName)}
                                                className="text-blue-600 hover:text-blue-800 text-sm flex items-center"
                                            >
                                                <Download className="w-4 h-4 mr-1" /> View
                                            </button>
                                        </div>
                                    ))}
                                </div>
                            ) : (
                                <p className="text-gray-500 italic">No documents attached.</p>
                            )}
                        </div>
                    </div>

                    {/* Actions */}
                    <div className="bg-gray-100 px-6 py-4 flex justify-end space-x-3">
                        <button
                            onClick={() => setShowReturn(true)}
                            className="px-4 py-2 border border-red-300 text-red-700 bg-white hover:bg-red-50 rounded flex items-center"
                        >
                            <XCircle className="w-4 h-4 mr-2" /> Return to Student
                        </button>
                        <button
                            onClick={() => setShowForwardConfirm(true)}
                            className="px-4 py-2 bg-green-600 text-white hover:bg-green-700 rounded flex items-center shadow"
                        >
                            <CheckCircle className="w-4 h-4 mr-2" /> Forward to Faculty
                        </button>
                    </div>
                </div>
            </div>
        );
    };

    return (
        <div className="space-y-6">
            <div className="flex justify-between items-center">
                <h2 className="text-2xl font-bold text-gray-800">Student Affairs (ÖİDB) Dashboard</h2>
                <div className="flex space-x-2">
                    <button
                        onClick={() => setViewMode('NEW')}
                        className={`px-4 py-2 rounded text-sm font-medium transition ${viewMode === 'NEW' ? 'bg-blue-900 text-white' : 'bg-white text-gray-600 border'}`}
                    >
                        New Applications
                    </button>
                    <button
                        onClick={() => setViewMode('RESUBMITTED')}
                        className={`px-4 py-2 rounded text-sm font-medium transition ${viewMode === 'RESUBMITTED' ? 'bg-blue-900 text-white' : 'bg-white text-gray-600 border'}`}
                    >
                        Resubmitted
                    </button>
                    <button
                        onClick={() => setViewMode('ANNOUNCEMENTS')}
                        className={`px-4 py-2 rounded text-sm font-medium transition flex items-center ${viewMode === 'ANNOUNCEMENTS' ? 'bg-blue-900 text-white' : 'bg-white text-gray-600 border'}`}
                    >
                        <Bell className="w-4 h-4 mr-1" /> Announcements
                    </button>
                    <button
                        onClick={() => setViewMode('PUBLISH')}
                        className={`px-4 py-2 rounded text-sm font-medium transition flex items-center ${viewMode === 'PUBLISH' ? 'bg-red-800 text-white' : 'bg-white text-gray-600 border'}`}
                    >
                        <FileText className="w-4 h-4 mr-1" /> Publish Results
                    </button>
                </div>
            </div>

            {viewMode === 'ANNOUNCEMENTS' ? (
                <AdminAnnouncementPanel />
            ) : viewMode === 'PUBLISH' ? (
                <PublishResultsPanel />
            ) : (
                <div className="bg-white rounded-lg shadow overflow-hidden">
                    <div className="p-0">
                        {loading ? <div className="p-8 text-center text-gray-500">Loading applications...</div> :
                            applications.length === 0 ? (
                                <div className="p-12 text-center">
                                    <div className="bg-gray-100 rounded-full w-16 h-16 flex items-center justify-center mx-auto mb-4">
                                        <CheckCircle className="text-gray-400 w-8 h-8" />
                                    </div>
                                    <h3 className="text-lg font-medium text-gray-900">All caught up!</h3>
                                    <p className="text-gray-500">No {viewMode.toLowerCase()} applications pending validation.</p>
                                </div>
                            ) : (
                                <table className="w-full text-left">
                                    <thead className="bg-gray-50 text-gray-600 text-sm border-b">
                                        <tr>
                                            <th className="px-6 py-3">ID</th>
                                            <th className="px-6 py-3">Student</th>
                                            <th className="px-6 py-3">Department</th>
                                            <th className="px-6 py-3">Score</th>
                                            <th className="px-6 py-3">Date</th>
                                            <th className="px-6 py-3">Action</th>
                                        </tr>
                                    </thead>
                                    <tbody className="divide-y divide-gray-200">
                                        {applications.map((app) => (
                                            <tr key={app.trackingId} className="hover:bg-blue-50 transition">
                                                <td className="px-6 py-4 font-mono text-xs text-gray-500">#{app.trackingId}</td>
                                                <td className="px-6 py-4 font-medium">{app.studentName}</td>
                                                <td className="px-6 py-4 text-sm text-gray-600">{app.departmentName}</td>
                                                <td className="px-6 py-4 font-bold text-blue-900">{app.compositeScore}</td>
                                                <td className="px-6 py-4 text-sm text-gray-500">{new Date(app.submissionDate).toLocaleDateString()}</td>
                                                <td className="px-6 py-4">
                                                    <button
                                                        onClick={() => setSelectedApp(app)}
                                                        className="text-blue-600 hover:text-blue-900 font-medium flex items-center"
                                                    >
                                                        Review <ArrowRight className="ml-1 w-4 h-4" />
                                                    </button>
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            )}
                    </div>
                </div>
            )}

            {selectedApp && <DetailModal app={selectedApp} onClose={() => setSelectedApp(null)} />}
        </div>
    );
}
