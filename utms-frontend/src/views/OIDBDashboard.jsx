import React from 'react';

export default function OIDBDashboard({ user }) {
    return (
        <div className="bg-white rounded shadow p-10 text-center">
            <h2 className="text-2xl font-bold mb-4">Student Affairs (ÖİDB) Dashboard</h2>
            <p className="text-gray-600">The validation interface will be implemented here (WP-4).</p>
            <div className="mt-8 p-4 bg-blue-50 border border-blue-200 rounded inline-block text-left">
                <h3 className="font-bold text-blue-800 mb-2">Pending Features:</h3>
                <ul className="list-disc list-inside text-sm text-blue-700">
                    <li>View Incoming Applications</li>
                    <li>Forward to Faculty</li>
                    <li>Return for Correction</li>
                </ul>
            </div>
        </div>
    );
}
