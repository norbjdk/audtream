import React from 'react';

const RoleButton = ({ value, selected, onClick }) => {
    return (
        <button
            type="button"
            onClick={() => onClick(value)}
            className={`flex-1 flex flex-col m-4 items-center justify-center p-4 rounded-xl transition-all duration-300 ${
                selected === value 
                ? 'bg-purple-900 border-2 border-purple-600' 
                : 'bg-[#2a2a2a] border-2 border-transparent hover:border-purple-800'
            }`}
        >
            <span className="text-white text-lg font-bold mb-2">
                {value === "Listener" ? "👂 Listener" : "🎤 Artist"}
            </span>
            <span className="text-gray-400 text-sm text-center">
                {value === "Listener" 
                    ? "Listen to music, create playlists" 
                    : "Upload tracks, manage your music"}
            </span>
        </button>
    );
};

export default RoleButton;