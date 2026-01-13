import React from 'react';

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
            <div className="text-gray-700 whitespace-pre-wrap">{content}</div>
        </div>
    );
};

export default AnnouncementCard;
