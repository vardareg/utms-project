import React, { useState, useEffect } from 'react';
import { Upload, FileText, CheckCircle, AlertCircle, Send, Loader } from 'lucide-react';
import { apiFetch, API_URL, getAuthHeader, getMyProfile } from '../services/api';

import NewsFeed from '../components/NewsFeed';
import ProfileEntryForm from '../components/ProfileEntryForm';

export default function StudentDashboard({ user }) {
    const [formData, setFormData] = useState({
        targetDepartmentId: 1, // Default to Computer Engineering (MVP)
        yksScore: '',
    });
    const [files, setFiles] = useState({
        transcript: null,
        yksResult: null,
        englishProof: null
    });
    const [status, setStatus] = useState({ loading: false, success: null, error: null });
    const [existingApp, setExistingApp] = useState(null);

    // Profile State
    const [profile, setProfile] = useState(null);
    const [loadingProfile, setLoadingProfile] = useState(true);

    useEffect(() => {
        loadProfile();
    }, []);

    const loadProfile = async () => {
        try {
            setLoadingProfile(true);
            const data = await getMyProfile();
            setProfile(data); // null if 204 (not found), object if found
        } catch (error) {
            console.error("Failed to load profile", error);
        } finally {
            setLoadingProfile(false);
        }
    };

    const handleInputChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleFileChange = (e) => {
        setFiles({ ...files, [e.target.name]: e.target.files[0] });
    };

    const handleRetrieveYksScore = async () => {
        try {
            setStatus({ loading: true, success: null, error: null });
            const response = await apiFetch('/applications/my-yks-score');
            if (response && response.score) {
                setFormData(prev => ({ ...prev, yksScore: response.score }));
                setStatus({ loading: false, success: "YKS Score retrieved successfully from ÖSYM.", error: null });
            } else {
                setStatus({ loading: false, success: null, error: "Could not retrieve score." });
            }
        } catch (err) {
            setStatus({ loading: false, success: null, error: "Failed to retrieve YKS Score: " + err.message });
        }
    };

    const uploadFile = async (appId, type, file) => {
        if (!file) return;
        const form = new FormData();
        form.append('applicationId', appId);
        form.append('documentType', type);
        form.append('file', file);

        // Native fetch for Multipart because API wrapper sets Content-Type to JSON
        const response = await fetch(`${API_URL}/documents/upload`, {
            method: 'POST',
            headers: { ...getAuthHeader() }, // Don't set Content-Type, let browser set boundary
            body: form
        });

        if (!response.ok) {
            const txt = await response.text();
            throw new Error(`Failed to upload ${type}: ${txt}`);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setStatus({ loading: true, success: null, error: null });

        if (!files.transcript || !files.yksResult || !files.englishProof) {
            setStatus({ loading: false, success: null, error: "Please upload Transcript, YKS Result, and English Proficiency Proof." });
            return;
        }

        try {
            // 1. Create Application
            const appResponse = await apiFetch('/applications', {
                method: 'POST',
                body: JSON.stringify({
                    targetDepartmentId: parseInt(formData.targetDepartmentId),
                    yksScore: parseFloat(formData.yksScore)
                })
            });

            const appId = appResponse.trackingId;

            // 2. Upload Documents
            await uploadFile(appId, 'TRANSCRIPT', files.transcript);
            await uploadFile(appId, 'YKS_RESULT', files.yksResult);
            await uploadFile(appId, 'ENGLISH_PROOF', files.englishProof);

            setStatus({ loading: false, success: `Application submitted successfully! Tracking ID: #${appId}`, error: null });
            setExistingApp(appResponse); // Show read-only view or success state

        } catch (err) {
            setStatus({ loading: false, success: null, error: err.message });
        }
    };

    // LOADING STATE
    if (loadingProfile) {
        return (
            <div className="flex justify-center items-center h-64">
                <Loader className="animate-spin w-10 h-10 text-red-900" />
            </div>
        );
    }

    // MISSING PROFILE STATE -> Show Entry Form
    if (!profile) {
        return <ProfileEntryForm onProfileCreated={loadProfile} />;
    }

    // SUCCESS STATE (Application Submitted)
    if (existingApp) {
        return (
            <div className="max-w-2xl mx-auto bg-green-50 border border-green-200 rounded-lg p-8 text-center">
                <CheckCircle className="w-16 h-16 text-green-600 mx-auto mb-4" />
                <h2 className="text-2xl font-bold text-green-900 mb-2">Application Received!</h2>
                <p className="text-green-800 mb-4">{status.success}</p>
                <div className="bg-white p-4 rounded shadow-sm inline-block text-left text-sm">
                    <p><strong>Status:</strong> Forwarded to Student Affairs</p>
                    <p><strong>Date:</strong> {new Date().toLocaleDateString()}</p>
                </div>
                <button
                    onClick={() => window.location.reload()}
                    className="block mx-auto mt-6 text-green-700 underline hover:text-green-900"
                >
                    Submit Another Application (Debug)
                </button>
            </div>
        );
    }

    // MAIN DASHBOARD (Application Form)
    return (
        <div className="max-w-4xl mx-auto space-y-8">
            {/* Announcements Section */}
            <section className="bg-white rounded-lg shadow p-6">
                <h2 className="text-xl font-bold text-gray-800 mb-4 border-b pb-2">Latest Announcements</h2>
                <NewsFeed />
            </section>

            {/* Profile Summary Card */}
            <section className="bg-blue-50 rounded-lg shadow p-4 border border-blue-100 flex justify-between items-center">
                <div>
                    <h3 className="font-bold text-blue-900">Student Profile Active</h3>
                    <p className="text-sm text-blue-800">
                        {profile.currentUniversity} - {profile.currentProgram} (GPA: {profile.overallGpa})
                    </p>
                </div>
                <div className="text-sm text-blue-600">
                    TCKN: {profile.tckn}
                </div>
            </section>

            <div className="bg-white rounded-lg shadow-lg overflow-hidden">
                <div className="bg-red-900 px-6 py-4 border-b border-red-800">
                    <h2 className="text-xl font-bold text-white flex items-center">
                        <FileText className="mr-2" /> Transfer Application Form
                    </h2>
                    <p className="text-red-100 text-sm mt-1">Please fill out all fields accurately.</p>
                </div>

                <div className="p-8">
                    {status.error && (
                        <div className="mb-6 bg-red-50 text-red-700 p-4 rounded flex items-start">
                            <AlertCircle className="w-5 h-5 mr-2 mt-0.5 flex-shrink-0" />
                            <span>{status.error}</span>
                        </div>
                    )}
                    {status.success && !existingApp && (
                        <div className="mb-6 bg-green-50 text-green-700 p-4 rounded flex items-start">
                            <CheckCircle className="w-5 h-5 mr-2 mt-0.5 flex-shrink-0" />
                            <span>{status.success}</span>
                        </div>
                    )}

                    <form onSubmit={handleSubmit} className="space-y-6">
                        {/* Personal Info (Read-Only from Profile now ideally, but keeping user info for now) */}
                        <div className="bg-gray-50 p-4 rounded border border-gray-200">
                            <h3 className="text-sm font-bold text-gray-500 uppercase mb-2">Applicant Information</h3>
                            <div className="grid grid-cols-2 gap-4">
                                <div>
                                    <label className="block text-xs text-gray-400">Username</label>
                                    <p className="font-medium">{user.username}</p>
                                </div>
                                <div>
                                    <label className="block text-xs text-gray-400">Date</label>
                                    <p className="font-medium">{new Date().toLocaleDateString()}</p>
                                </div>
                            </div>
                        </div>

                        {/* Academic Info */}
                        <div>
                            <h3 className="text-lg font-semibold text-gray-800 mb-4">Academic Details</h3>
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Target Department</label>
                                    <select
                                        name="targetDepartmentId"
                                        value={formData.targetDepartmentId}
                                        onChange={handleInputChange}
                                        className="w-full px-4 py-2 border rounded focus:ring-2 focus:ring-red-900 outline-none bg-white"
                                    >
                                        <option value="1">Computer Engineering</option>
                                        <option value="2">Mechanical Engineering (Mock)</option>
                                        <option value="3">Architecture (Mock)</option>
                                    </select>
                                </div>
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">YKS Score (0-500)</label>
                                    <div className="flex gap-2">
                                        <input
                                            type="number"
                                            name="yksScore"
                                            step="0.001"
                                            min="0"
                                            max="500"
                                            value={formData.yksScore}
                                            onChange={handleInputChange}
                                            placeholder="e.g. 485.500"
                                            className="w-full px-4 py-2 border rounded focus:ring-2 focus:ring-red-900 outline-none"
                                            required
                                        />
                                        <button
                                            type="button"
                                            onClick={handleRetrieveYksScore}
                                            className="bg-blue-600 text-white px-3 py-2 rounded hover:bg-blue-700 text-xs font-bold whitespace-nowrap flex items-center"
                                            disabled={status.loading}
                                        >
                                            {status.loading ? <Loader className="animate-spin w-4 h-4" /> : "Retrieve"}
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>

                        {/* File Uploads */}
                        <div>
                            <h3 className="text-lg font-semibold text-gray-800 mb-4">Required Documents</h3>
                            <div className="space-y-4">
                                <div className="border-2 border-dashed border-gray-300 rounded-lg p-6 hover:bg-gray-50 transition text-center">
                                    <label className="cursor-pointer block">
                                        <Upload className="mx-auto h-8 w-8 text-gray-400 mb-2" />
                                        <span className="text-sm font-medium text-gray-900">Upload Transcript</span>
                                        <span className="text-xs text-gray-500 block mt-1">PDF, Max 5MB</span>
                                        <input
                                            type="file"
                                            name="transcript"
                                            accept=".pdf"
                                            onChange={handleFileChange}
                                            className="hidden"
                                        />
                                        {files.transcript && (
                                            <div className="mt-2 text-sm text-green-600 font-medium flex items-center justify-center">
                                                <CheckCircle className="w-4 h-4 mr-1" /> {files.transcript.name}
                                            </div>
                                        )}
                                    </label>
                                </div>

                                <div className="border-2 border-dashed border-gray-300 rounded-lg p-6 hover:bg-gray-50 transition text-center">
                                    <label className="cursor-pointer block">
                                        <Upload className="mx-auto h-8 w-8 text-gray-400 mb-2" />
                                        <span className="text-sm font-medium text-gray-900">Upload YKS Result</span>
                                        <span className="text-xs text-gray-500 block mt-1">PDF, Max 5MB</span>
                                        <input
                                            type="file"
                                            name="yksResult"
                                            accept=".pdf"
                                            onChange={handleFileChange}
                                            className="hidden"
                                        />
                                        {files.yksResult && (
                                            <div className="mt-2 text-sm text-green-600 font-medium flex items-center justify-center">
                                                <CheckCircle className="w-4 h-4 mr-1" /> {files.yksResult.name}
                                            </div>
                                        )}
                                    </label>
                                </div>

                                <div className="border-2 border-dashed border-gray-300 rounded-lg p-6 hover:bg-gray-50 transition text-center">
                                    <label className="cursor-pointer block">
                                        <Upload className="mx-auto h-8 w-8 text-gray-400 mb-2" />
                                        <span className="text-sm font-medium text-gray-900">Upload English Proficiency Proof</span>
                                        <span className="text-xs text-gray-500 block mt-1">PDF, Max 5MB</span>
                                        <input
                                            type="file"
                                            name="englishProof"
                                            accept=".pdf"
                                            onChange={handleFileChange}
                                            className="hidden"
                                        />
                                        {files.englishProof && (
                                            <div className="mt-2 text-sm text-green-600 font-medium flex items-center justify-center">
                                                <CheckCircle className="w-4 h-4 mr-1" /> {files.englishProof.name}
                                            </div>
                                        )}
                                    </label>
                                </div>
                            </div>
                        </div>

                        {/* Submit Button */}
                        <div className="pt-4">
                            <button
                                type="submit"
                                disabled={status.loading}
                                className={`w-full bg-red-900 text-white font-bold py-3 px-4 rounded shadow hover:bg-red-800 transition flex justify-center items-center ${status.loading ? 'opacity-70 cursor-not-allowed' : ''}`}
                            >
                                {status.loading ? (
                                    <>
                                        <Loader className="animate-spin w-5 h-5 mr-2" /> Submitting Application...
                                    </>
                                ) : (
                                    <>
                                        <Send className="w-5 h-5 mr-2" /> Submit Application
                                    </>
                                )}
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
}
