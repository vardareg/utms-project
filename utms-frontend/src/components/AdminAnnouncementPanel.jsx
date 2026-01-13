import React, { useState, useEffect } from 'react';
import { apiFetch, API_URL } from '../services/api';

const AdminAnnouncementPanel = () => {
    const [announcements, setAnnouncements] = useState([]);
    const [formData, setFormData] = useState({
        title: '',
        content: '',
        priority: 'NORMAL',
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    useEffect(() => {
        fetchAnnouncements();
    }, []);

    const fetchAnnouncements = async () => {
        try {
            const data = await apiFetch(`/public/announcements`);
            setAnnouncements(data);
        } catch (err) {
            console.error(err);
        }
    };

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');
        setSuccess('');

        try {
            await apiFetch(`/oidb/announcements`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(formData),
            });
            setSuccess('Announcement published successfully!');
            setFormData({ title: '', content: '', priority: 'NORMAL' });
            fetchAnnouncements(); // Refresh list
        } catch (err) {
            setError(err.message || 'Failed to publish announcement');
        } finally {
            setLoading(false);
        }
    };

    const [deleteId, setDeleteId] = useState(null);

    const confirmDelete = (id) => {
        setDeleteId(id);
    };

    const cancelDelete = () => {
        setDeleteId(null);
    };

    const handleDelete = async () => {
        if (!deleteId) return;

        try {
            await apiFetch(`/oidb/announcements/${deleteId}`, {
                method: 'DELETE',
            });
            fetchAnnouncements();
            setSuccess('Announcement deleted successfully.');
        } catch (err) {
            setError(err.message || "Failed to delete announcement");
        } finally {
            setDeleteId(null);
        }
    };

    return (
        <div className="bg-white p-6 rounded shadow mb-6 relative">
            <h2 className="text-xl font-bold mb-4 text-gray-800 border-b pb-2">Manage Announcements</h2>

            {/* Create Form */}
            <form onSubmit={handleSubmit} className="mb-8 space-y-4">
                <div>
                    <label className="block text-sm font-medium text-gray-700">Title</label>
                    <input
                        type="text"
                        name="title"
                        value={formData.title}
                        onChange={handleChange}
                        className="mt-1 block w-full rounded border-gray-300 shadow-sm p-2 border"
                        required
                    />
                </div>
                <div>
                    <label className="block text-sm font-medium text-gray-700">Content</label>
                    <textarea
                        name="content"
                        value={formData.content}
                        onChange={handleChange}
                        className="mt-1 block w-full rounded border-gray-300 shadow-sm p-2 border h-24"
                        required
                    />
                </div>
                <div>
                    <label className="block text-sm font-medium text-gray-700">Priority</label>
                    <select
                        name="priority"
                        value={formData.priority}
                        onChange={handleChange}
                        className="mt-1 block w-full rounded border-gray-300 shadow-sm p-2 border"
                    >
                        <option value="LOW">Low</option>
                        <option value="NORMAL">Normal</option>
                        <option value="CRITICAL">Critical</option>
                    </select>
                </div>

                {error && <div className="text-red-500 text-sm">{error}</div>}
                {success && <div className="text-green-500 text-sm">{success}</div>}

                <button
                    type="submit"
                    disabled={loading}
                    className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 disabled:opacity-50"
                >
                    {loading ? 'Publishing...' : 'Publish Announcement'}
                </button>
            </form>

            <h3 className="text-lg font-semibold mb-3">Active Announcements</h3>
            <div className="space-y-3">
                {announcements.map((ann) => (
                    <div key={ann.id} className="flex justify-between items-center p-3 border rounded bg-gray-50">
                        <div>
                            <span className={`font-bold mr-2 ${ann.priority === 'CRITICAL' ? 'text-red-600' : 'text-gray-800'}`}>
                                {ann.title}
                            </span>
                            <span className="text-gray-500 text-sm">
                                ({new Date(ann.publishDate).toLocaleDateString()})
                            </span>
                        </div>
                        <button
                            onClick={() => confirmDelete(ann.id)}
                            className="text-red-600 hover:text-red-800 text-sm font-semibold"
                        >
                            Delete
                        </button>
                    </div>
                ))}
                {announcements.length === 0 && <p className="text-gray-500 italic">No active announcements.</p>}
            </div>

            {/* Delete Confirmation Modal */}
            {deleteId && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
                    <div className="bg-white p-6 rounded-lg shadow-xl max-w-sm w-full mx-4">
                        <h3 className="text-lg font-bold text-gray-900 mb-2">Confirm Deletion</h3>
                        <p className="text-gray-600 mb-6">
                            Are you sure you want to delete this announcement? This action cannot be undone.
                        </p>
                        <div className="flex justify-end space-x-3">
                            <button
                                onClick={cancelDelete}
                                className="px-4 py-2 text-gray-700 bg-gray-100 rounded hover:bg-gray-200 transition-colors"
                            >
                                Cancel
                            </button>
                            <button
                                onClick={handleDelete}
                                className="px-4 py-2 text-white bg-red-600 rounded hover:bg-red-700 transition-colors"
                            >
                                Delete
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default AdminAnnouncementPanel;
