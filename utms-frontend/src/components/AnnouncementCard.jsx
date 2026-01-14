import React from 'react';
import { API_URL } from '../services/api';

const AnnouncementCard = ({ announcement }) => {
    const { title, content, publishDate, priority } = announcement;

    // Format date nicely
    const formattedDate = new Date(publishDate).toLocaleDateString(undefined, {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });

    // Style mapping for priorities
    const priorityStyles = {
        CRITICAL: 'border-l-4 border-red-600 bg-red-50',
        NORMAL: 'border-l-4 border-blue-500 bg-white',
        LOW: 'border-l-4 border-gray-400 bg-gray-50',
    };

    const cardClass = `mb-4 p-4 rounded shadow-sm hover:shadow-md transition-shadow ${priorityStyles[priority] || priorityStyles.NORMAL}`;

    return (
        <div className={cardClass}>
            <div className="flex justify-between items-start mb-2">
                <h3 className="text-lg font-bold text-gray-800">{title}</h3>
                {priority === 'CRITICAL' && (
                    <span className="px-2 py-1 text-xs font-semibold text-red-700 bg-red-200 rounded-full">
                        IMPORTANT
                    </span>
                )}
            </div>
            <div className="text-sm text-gray-500 mb-3">{formattedDate}</div>
            <div className="text-gray-700 whitespace-pre-wrap mb-3">{content}</div>

            {announcement.downloadUrl && (
                <div className="mt-2">
                    <a
                        href={`${API_URL}${announcement.downloadUrl}`}
                        className="inline-flex items-center text-blue-600 hover:text-blue-800 font-medium"
                        target="_blank"
                        rel="noopener noreferrer"
                    >
                        <svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15.172 7l-6.586 6.586a2 2 0 102.828 2.828l6.414-6.586a4 4 0 00-5.656-5.656l-6.415 6.585a6 6 0 108.486 8.486L20.5 13"></path>
                        </svg>
                        Download Attachment {announcement.attachmentName ? `(${announcement.attachmentName})` : ''}
                    </a>
                </div>
            )}
        </div>
    );
};

export default AnnouncementCard;
