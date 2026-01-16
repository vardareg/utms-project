import React, { useEffect, useState } from "react";
import { getSystemStats } from "../services/api";

export default function SystemHealthPanel() {
    const [stats, setStats] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const fetchStats = async () => {
        try {
            const data = await getSystemStats();
            setStats(data);
            setError(null);
        } catch (err) {
            console.error("Failed to fetch system stats:", err);
            setError("Failed to load system status.");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchStats();
        // Refresh every 30 seconds
        const interval = setInterval(fetchStats, 30000);
        return () => clearInterval(interval);
    }, []);

    if (loading) {
        return <div className="p-4 text-center">Loading system health...</div>;
    }

    if (error) {
        return <div className="p-4 text-red-600 text-center">{error}</div>;
    }

    if (!stats) return null;

    return (
        <div className="space-y-6">
            <h2 className="text-xl font-semibold mb-4">System Performance & Health</h2>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
                {/* System Status Card */}
                <div className="bg-white p-6 rounded-lg shadow border-l-4 border-green-500">
                    <h3 className="text-gray-500 text-sm font-medium uppercase">System Status</h3>
                    <p className="text-2xl font-bold text-gray-800 mt-2">
                        {stats.systemHealth === "UP" ? "Online ✅" : "Issues ⚠️"}
                    </p>
                </div>

                {/* Uptime Card */}
                <div className="bg-white p-6 rounded-lg shadow border-l-4 border-blue-500">
                    <h3 className="text-gray-500 text-sm font-medium uppercase">Uptime</h3>
                    <p className="text-2xl font-bold text-gray-800 mt-2">{stats.uptimeFormatted}</p>
                </div>

                {/* Active Requests Card */}
                <div className="bg-white p-6 rounded-lg shadow border-l-4 border-purple-500">
                    <h3 className="text-gray-500 text-sm font-medium uppercase">Active Requests</h3>
                    <p className="text-2xl font-bold text-gray-800 mt-2">{stats.activeRequests}</p>
                </div>

                {/* Memory Info Card */}
                <div className="bg-white p-6 rounded-lg shadow border-l-4 border-orange-500">
                    <h3 className="text-gray-500 text-sm font-medium uppercase">Memory Usage</h3>
                    <p className="text-2xl font-bold text-gray-800 mt-2">{stats.usedMemoryInfo}</p>
                </div>
            </div>

            <div className="bg-gray-50 p-4 rounded-md text-sm text-gray-600 mt-6">
                <p><strong>DB Details:</strong> {JSON.stringify(stats.dbDetails)}</p>
                <p className="mt-1">Last Updated: {new Date().toLocaleTimeString()}</p>
            </div>
        </div>
    );
}
