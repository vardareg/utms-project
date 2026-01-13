import React, { useEffect, useState } from 'react';
import AnnouncementCard from './AnnouncementCard';
import { apiFetch, API_URL } from '../services/api';

const NewsFeed = () => {
    const [announcements, setAnnouncements] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchAnnouncements = async () => {
            try {
                // Assuming apiFetch handles the base URL and headers if needed
                // If public endpoint is open, simple fetch might suffice, but sticking to apiFetch for consistency
                const data = await apiFetch(`/public/announcements`);
                setAnnouncements(data);
            } catch (err) {
                console.error("Failed to fetch announcements:", err);
                setError("Could not load announcements.");
            } finally {
                setLoading(false);
            }
        };

        fetchAnnouncements();
    }, []);

    if (loading) return <div className="text-gray-500 text-center py-4">Loading updates...</div>;
    if (error) return <div className="text-red-500 text-center py-4">{error}</div>;
    if (announcements.length === 0) return <div className="text-gray-500 text-center py-4">No active announcements.</div>;

    return (
        <div className="space-y-4">
            {announcements.map((ann) => (
                <AnnouncementCard key={ann.id} announcement={ann} />
            ))}
        </div>
    );
};

export default NewsFeed;
