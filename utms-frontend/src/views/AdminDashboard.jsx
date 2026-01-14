import React, { useState } from 'react';
import AdminRulesPage from './AdminRulesPage';
import AdminAnnouncementPanel from '../components/AdminAnnouncementPanel';
import { Settings, Megaphone } from 'lucide-react';

export default function AdminDashboard({ user }) {
    const [activeTab, setActiveTab] = useState('rules');

    return (
        <div className="space-y-6">
            {/* Top Bar: Title + Navigation Toggle */}
            <div className="flex justify-between items-center">
                <h2 className="text-2xl font-bold text-gray-800">Admin Configuration Console</h2>
                <div className="flex space-x-2">
                    <button
                        onClick={() => setActiveTab('rules')}
                        className={`px-4 py-2 rounded text-sm font-medium transition flex items-center ${activeTab === 'rules'
                                ? 'bg-red-900 text-white'
                                : 'bg-white text-gray-600 border hover:bg-gray-50'
                            }`}
                    >
                        <Settings size={16} className="mr-2" />
                        System Rules
                    </button>
                    <button
                        onClick={() => setActiveTab('announcements')}
                        className={`px-4 py-2 rounded text-sm font-medium transition flex items-center ${activeTab === 'announcements'
                                ? 'bg-red-900 text-white'
                                : 'bg-white text-gray-600 border hover:bg-gray-50'
                            }`}
                    >
                        <Megaphone size={16} className="mr-2" />
                        Announcements
                    </button>
                </div>
            </div>

            {/* Main Content Area */}
            <div className="bg-white rounded-lg shadow min-h-[500px] p-6">
                {activeTab === 'rules' && (
                    <div className="animate-fade-in-up">
                        <AdminRulesPage isEmbedded={true} />
                    </div>
                )}

                {activeTab === 'announcements' && (
                    <div className="animate-fade-in-up">
                        <AdminAnnouncementPanel user={user} />
                    </div>
                )}
            </div>
        </div>
    );
}
