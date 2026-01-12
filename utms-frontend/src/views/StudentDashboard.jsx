import React from 'react';

export default function StudentDashboard({ user }) {
    return (
        <div className="bg-white rounded shadow p-10 text-center">
            <h2 className="text-2xl font-bold mb-4">Student Application Portal</h2>
            <p className="text-gray-600">The application form will be implemented here (WP-3.3).</p>
            <div className="mt-8 p-4 bg-yellow-50 border border-yellow-200 rounded inline-block text-left">
                <h3 className="font-bold text-yellow-800 mb-2">Pending Features:</h3>
                <ul className="list-disc list-inside text-sm text-yellow-700">
                    <li>YKS Score Input</li>
                    <li>Department Selection</li>
                    <li>Transcript Upload</li>
                    <li>YKS Result Upload</li>
                    <li>Submit Application</li>
                </ul>
            </div>
        </div>
    );
}
