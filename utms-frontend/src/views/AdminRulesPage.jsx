import React, { useState, useEffect } from 'react';
import { apiFetch } from '../services/api';
import { Save, AlertTriangle, CheckCircle } from 'lucide-react';

export default function AdminRulesPage() {
    const [configs, setConfigs] = useState({
        MIN_GPA_THRESHOLD: '2.50',
        MIN_YKS_THRESHOLD: '150.00',
        WEIGHT_GPA: '0.5',
        WEIGHT_YKS: '0.5'
    });
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    useEffect(() => {
        loadConfigs();
    }, []);

    const loadConfigs = async () => {
        setLoading(true);
        try {
            const data = await apiFetch('/admin/config');
            // Merge with defaults in case keys missing
            setConfigs(prev => ({ ...prev, ...data }));
        } catch (err) {
            setError('Failed to load configurations: ' + err.message);
        } finally {
            setLoading(false);
        }
    };

    const handleChange = (key, value) => {
        setConfigs(prev => ({ ...prev, [key]: value }));
    };

    const handleSave = async (e) => {
        e.preventDefault();
        setSaving(true);
        setError('');
        setSuccess('');

        try {
            await apiFetch('/admin/config', {
                method: 'PUT',
                body: JSON.stringify(configs)
            });
            setSuccess('Configuration updated successfully.');
        } catch (err) {
            setError('Failed to update: ' + err.message);
        } finally {
            setSaving(false);
        }
    };

    if (loading) return <div className="p-8 text-center text-gray-500">Loading system rules...</div>;

    const totalWeight = (parseFloat(configs.WEIGHT_GPA || 0) + parseFloat(configs.WEIGHT_YKS || 0)).toFixed(2);

    return (
        <div className="max-w-2xl mx-auto bg-white shadow-lg rounded-lg p-6 mt-8">
            <h2 className="text-2xl font-bold text-gray-800 mb-6 flex items-center">
                System Rules Configuration
            </h2>

            {error && (
                <div className="bg-red-50 text-red-700 p-4 rounded mb-6 flex items-center">
                    <AlertTriangle className="mr-2" size={20} />
                    {error}
                </div>
            )}

            {success && (
                <div className="bg-green-50 text-green-700 p-4 rounded mb-6 flex items-center">
                    <CheckCircle className="mr-2" size={20} />
                    {success}
                </div>
            )}

            <form onSubmit={handleSave}>
                <div className="grid grid-cols-2 gap-4 mb-6">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            Min. GPA Threshold (4.0 Scale)
                        </label>
                        <input
                            type="number"
                            step="0.01"
                            min="0"
                            max="4"
                            value={configs.MIN_GPA_THRESHOLD}
                            onChange={(e) => handleChange('MIN_GPA_THRESHOLD', e.target.value)}
                            className="w-full p-2 border border-gray-300 rounded focus:border-red-500 focus:ring-1 focus:ring-red-500"
                        />
                        <p className="text-xs text-gray-500 mt-1">
                            Min GPA to Apply.
                        </p>
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            Min. YKS Score
                        </label>
                        <input
                            type="number"
                            step="0.01"
                            min="0"
                            max="600"
                            value={configs.MIN_YKS_THRESHOLD}
                            onChange={(e) => handleChange('MIN_YKS_THRESHOLD', e.target.value)}
                            className="w-full p-2 border border-gray-300 rounded focus:border-red-500 focus:ring-1 focus:ring-red-500"
                        />
                        <p className="text-xs text-gray-500 mt-1">
                            Min YKS Score to Apply.
                        </p>
                    </div>
                </div>

                <div className="grid grid-cols-2 gap-4 mb-6">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            Weight: GPA (0-1)
                        </label>
                        <input
                            type="number"
                            step="0.01"
                            min="0"
                            max="1"
                            value={configs.WEIGHT_GPA}
                            onChange={(e) => handleChange('WEIGHT_GPA', e.target.value)}
                            className="w-full p-2 border border-gray-300 rounded focus:border-red-500 focus:ring-1 focus:ring-red-500"
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            Weight: YKS (0-1)
                        </label>
                        <input
                            type="number"
                            step="0.01"
                            min="0"
                            max="1"
                            value={configs.WEIGHT_YKS}
                            onChange={(e) => handleChange('WEIGHT_YKS', e.target.value)}
                            className="w-full p-2 border border-gray-300 rounded focus:border-red-500 focus:ring-1 focus:ring-red-500"
                        />
                    </div>
                </div>

                <div className="flex justify-between items-center bg-gray-50 p-4 rounded mb-6">
                    <span className="text-sm font-medium text-gray-600">Total Weight:</span>
                    <span className={`font-bold ${totalWeight === '1.00' ? 'text-green-600' : 'text-orange-500'}`}>
                        {totalWeight}
                    </span>
                </div>

                <div className="flex justify-end space-x-3">
                    <button
                        type="submit"
                        disabled={saving}
                        className="flex items-center space-x-2 bg-red-900 text-white px-6 py-2 rounded hover:bg-red-800 disabled:opacity-50 transition"
                    >
                        <Save size={18} />
                        <span>{saving ? 'Saving...' : 'Save Configuration'}</span>
                    </button>
                </div>
            </form>
        </div>
    );
}
