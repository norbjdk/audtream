import { useState, useRef, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { tracksAPI } from '../services/api';

function Upload() {
    const { user } = useAuth();
    const navigate = useNavigate();
    
    // State dla formularza
    const [title, setTitle] = useState('');
    const [artist, setArtist] = useState(user?.username || '');
    const [album, setAlbum] = useState('');
    const [duration, setDuration] = useState('');
    const [genre, setGenre] = useState('');
    const [description, setDescription] = useState('');
    
    // State dla pliku
    const [audioFile, setAudioFile] = useState(null);
    const [audioPreview, setAudioPreview] = useState(null);
    const [coverFile, setCoverFile] = useState(null);
    const [coverPreview, setCoverPreview] = useState(null);
    
    // State dla uploadu
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [uploadProgress, setUploadProgress] = useState(0);
    
    // Refs
    const audioInputRef = useRef(null);
    const coverInputRef = useRef(null);
    
    // Genres lista
    const genres = [
        'Rock', 'Pop', 'Hip Hop', 'Jazz', 'Classical', 'Electronic', 
        'R&B', 'Country', 'Metal', 'Folk', 'Blues', 'Reggae',
        'Punk', 'Funk', 'Soul', 'Disco', 'Techno', 'House',
        'Trance', 'Drum & Bass', 'Dubstep', 'Trap', 'Lo-fi', 'Indie'
    ];

    // Obsługa wyboru pliku audio
    const handleAudioFileChange = (e) => {
        const file = e.target.files[0];
        if (!file) return;
        
        // Sprawdź typ pliku
        if (!file.type.startsWith('audio/')) {
            setError('Please select an audio file (MP3, WAV, FLAC, etc.)');
            return;
        }
        
        // Sprawdź rozmiar pliku (max 50MB)
        if (file.size > 50 * 1024 * 1024) {
            setError('Audio file is too large (max 50MB)');
            return;
        }
        
        setAudioFile(file);
        setError('');
        
        // Utwórz podgląd audio
        const objectUrl = URL.createObjectURL(file);
        setAudioPreview(objectUrl);
        
        // Pobierz metadane audio (długość)
        const audio = new Audio(objectUrl);
        audio.addEventListener('loadedmetadata', () => {
            setDuration(Math.round(audio.duration));
        });
    };

    // Obsługa wyboru okładki
    const handleCoverFileChange = (e) => {
        const file = e.target.files[0];
        if (!file) return;
        
        // Sprawdź typ pliku
        if (!file.type.startsWith('image/')) {
            setError('Please select an image file (JPG, PNG, etc.)');
            return;
        }
        
        // Sprawdź rozmiar pliku (max 5MB)
        if (file.size > 5 * 1024 * 1024) {
            setError('Cover image is too large (max 5MB)');
            return;
        }
        
        setCoverFile(file);
        
        // Utwórz podgląd okładki
        const reader = new FileReader();
        reader.onloadend = () => {
            setCoverPreview(reader.result);
        };
        reader.readAsDataURL(file);
    };

    // Format czasu (sekundy -> MM:SS)
    const formatDuration = (seconds) => {
        if (!seconds) return '00:00';
        const mins = Math.floor(seconds / 60);
        const secs = Math.floor(seconds % 60);
        return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
    };

    // Format rozmiaru pliku
    const formatFileSize = (bytes) => {
        if (bytes === 0) return '0 Bytes';
        const k = 1024;
        const sizes = ['Bytes', 'KB', 'MB', 'GB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    };

    // Obsługa submit formularza
    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');
        
        // Walidacja
        if (!title.trim()) {
            setError('Please enter a track title');
            return;
        }
        
        if (!audioFile) {
            setError('Please select an audio file to upload');
            return;
        }
        
        setLoading(true);
        setUploadProgress(0);
        
        try {
            // Symulacja progressu (w rzeczywistości byłoby z axios interceptors)
            const progressInterval = setInterval(() => {
                setUploadProgress(prev => {
                    if (prev >= 90) {
                        clearInterval(progressInterval);
                        return prev;
                    }
                    return prev + 10;
                });
            }, 200);
            
            // Tutaj w rzeczywistości wysyłamy plik do backendu
            // Na razie tworzymy track z danymi (bez faktycznego uploadu pliku)
            const trackData = {
                title: title.trim(),
                artist: artist.trim(),
                album: album.trim(),
                duration: parseInt(duration) || 0,
                genre: genre,
                filePath: 'temporary/path.mp3', // W prawdziwej aplikacji to byłby upload
                description: description.trim()
            };
            
            const response = await tracksAPI.createTrack(trackData);
            
            clearInterval(progressInterval);
            setUploadProgress(100);
            
            setSuccess('Track uploaded successfully!');
            
            // Reset formularza po 3 sekundach
            setTimeout(() => {
                resetForm();
                navigate('/library');
            }, 3000);
            
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to upload track');
        } finally {
            setLoading(false);
        }
    };

    // Reset formularza
    const resetForm = () => {
        setTitle('');
        setArtist(user?.username || '');
        setAlbum('');
        setDuration('');
        setGenre('');
        setDescription('');
        setAudioFile(null);
        setCoverFile(null);
        setAudioPreview(null);
        setCoverPreview(null);
        setUploadProgress(0);
        
        if (audioInputRef.current) audioInputRef.current.value = '';
        if (coverInputRef.current) coverInputRef.current.value = '';
    };

    // Cleanup preview URLs
    useEffect(() => {
        return () => {
            if (audioPreview) URL.revokeObjectURL(audioPreview);
        };
    }, [audioPreview]);

    // Sprawdź czy użytkownik jest artystą
    if (user?.role !== 'Artist') {
        return (
            <div className="min-h-screen bg-gradient-to-b from-gray-900 to-black text-white p-8 mt-20">
                <div className="max-w-4xl mx-auto">
                    <div className="bg-gray-800/50 backdrop-blur-sm rounded-2xl p-8 text-center">
                        <div className="text-6xl mb-4">🎤</div>
                        <h1 className="text-3xl font-bold mb-4">Artist Access Required</h1>
                        <p className="text-gray-300 mb-6">
                            You need to have an Artist account to upload tracks.
                        </p>
                        <p className="text-gray-400 mb-8">
                            Current role: <span className="font-bold text-purple-400">{user?.role || 'Not logged in'}</span>
                        </p>
                        <div className="flex gap-4 justify-center">
                            <button
                                onClick={() => navigate('/')}
                                className="px-6 py-3 bg-gray-700 hover:bg-gray-600 rounded-lg transition"
                            >
                                Go Home
                            </button>
                            <button
                                onClick={() => navigate('/profile')}
                                className="px-6 py-3 bg-purple-700 hover:bg-purple-600 rounded-lg transition"
                            >
                                Upgrade to Artist
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gradient-to-b from-gray-900 to-black mt-20 text-white p-4 md:p-8">
            <div className="max-w-6xl mx-auto">
                {/* Nagłówek */}
                <div className="mb-8">
                    <h1 className="text-3xl md:text-4xl font-bold mb-2">Upload New Track</h1>
                    <p className="text-gray-400">Share your music with the world</p>
                </div>

                {/* Pasek postępu */}
                {uploadProgress > 0 && uploadProgress < 100 && (
                    <div className="mb-6">
                        <div className="flex justify-between text-sm text-gray-400 mb-1">
                            <span>Uploading...</span>
                            <span>{uploadProgress}%</span>
                        </div>
                        <div className="w-full bg-gray-800 rounded-full h-2">
                            <div 
                                className="bg-gradient-to-r from-purple-600 to-pink-600 h-2 rounded-full transition-all duration-300"
                                style={{ width: `${uploadProgress}%` }}
                            ></div>
                        </div>
                    </div>
                )}

                {/* Wiadomości */}
                {error && (
                    <div className="mb-6 p-4 bg-red-900/30 border border-red-700 rounded-lg">
                        <p className="text-red-300">{error}</p>
                    </div>
                )}
                
                {success && (
                    <div className="mb-6 p-4 bg-green-900/30 border border-green-700 rounded-lg">
                        <p className="text-green-300">{success}</p>
                    </div>
                )}

                {/* Formularz */}
                <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
                    {/* Lewa kolumna - Formularz */}
                    <div className="bg-gray-800/50 backdrop-blur-sm rounded-2xl p-6">
                        <form onSubmit={handleSubmit} className="space-y-6">
                            {/* Tytuł */}
                            <div>
                                <label className="block text-sm font-medium text-gray-300 mb-2">
                                    Track Title *
                                </label>
                                <input
                                    type="text"
                                    value={title}
                                    onChange={(e) => setTitle(e.target.value)}
                                    className="w-full px-4 py-3 bg-gray-900/70 border border-gray-700 rounded-lg text-white placeholder-gray-500 focus:outline-none focus:border-purple-600 focus:ring-1 focus:ring-purple-600"
                                    placeholder="Enter track title"
                                    required
                                />
                            </div>

                            {/* Artysta */}
                            <div>
                                <label className="block text-sm font-medium text-gray-300 mb-2">
                                    Artist
                                </label>
                                <input
                                    type="text"
                                    value={artist}
                                    onChange={(e) => setArtist(e.target.value)}
                                    className="w-full px-4 py-3 bg-gray-900/70 border border-gray-700 rounded-lg text-white placeholder-gray-500 focus:outline-none focus:border-purple-600 focus:ring-1 focus:ring-purple-600"
                                    placeholder="Artist name"
                                />
                            </div>

                            {/* Album */}
                            <div>
                                <label className="block text-sm font-medium text-gray-300 mb-2">
                                    Album (optional)
                                </label>
                                <input
                                    type="text"
                                    value={album}
                                    onChange={(e) => setAlbum(e.target.value)}
                                    className="w-full px-4 py-3 bg-gray-900/70 border border-gray-700 rounded-lg text-white placeholder-gray-500 focus:outline-none focus:border-purple-600 focus:ring-1 focus:ring-purple-600"
                                    placeholder="Album name"
                                />
                            </div>

                            {/* Gatunek */}
                            <div>
                                <label className="block text-sm font-medium text-gray-300 mb-2">
                                    Genre
                                </label>
                                <select
                                    value={genre}
                                    onChange={(e) => setGenre(e.target.value)}
                                    className="w-full px-4 py-3 bg-gray-900/70 border border-gray-700 rounded-lg text-white focus:outline-none focus:border-purple-600 focus:ring-1 focus:ring-purple-600"
                                >
                                    <option value="">Select genre</option>
                                    {genres.map((g) => (
                                        <option key={g} value={g}>{g}</option>
                                    ))}
                                </select>
                            </div>

                            {/* Opis */}
                            <div>
                                <label className="block text-sm font-medium text-gray-300 mb-2">
                                    Description (optional)
                                </label>
                                <textarea
                                    value={description}
                                    onChange={(e) => setDescription(e.target.value)}
                                    rows="3"
                                    className="w-full px-4 py-3 bg-gray-900/70 border border-gray-700 rounded-lg text-white placeholder-gray-500 focus:outline-none focus:border-purple-600 focus:ring-1 focus:ring-purple-600 resize-none"
                                    placeholder="Tell us about this track..."
                                />
                            </div>

                            {/* Plik audio */}
                            <div>
                                <label className="block text-sm font-medium text-gray-300 mb-2">
                                    Audio File *
                                </label>
                                <div className="border-2 border-dashed border-gray-700 rounded-lg p-6 text-center hover:border-purple-600 transition-colors cursor-pointer"
                                     onClick={() => audioInputRef.current?.click()}>
                                    <input
                                        type="file"
                                        ref={audioInputRef}
                                        onChange={handleAudioFileChange}
                                        accept="audio/*"
                                        className="hidden"
                                    />
                                    <div className="text-4xl mb-2">🎵</div>
                                    <p className="text-gray-300 mb-1">
                                        {audioFile ? audioFile.name : 'Click to upload audio'}
                                    </p>
                                    <p className="text-gray-500 text-sm">
                                        MP3, WAV, FLAC, AAC (max 50MB)
                                    </p>
                                    {audioFile && (
                                        <p className="text-gray-400 text-sm mt-2">
                                            {formatFileSize(audioFile.size)} • {formatDuration(duration)}
                                        </p>
                                    )}
                                </div>
                            </div>

                            {/* Okładka */}
                            <div>
                                <label className="block text-sm font-medium text-gray-300 mb-2">
                                    Cover Image (optional)
                                </label>
                                <div className="border-2 border-dashed border-gray-700 rounded-lg p-6 text-center hover:border-purple-600 transition-colors cursor-pointer"
                                     onClick={() => coverInputRef.current?.click()}>
                                    <input
                                        type="file"
                                        ref={coverInputRef}
                                        onChange={handleCoverFileChange}
                                        accept="image/*"
                                        className="hidden"
                                    />
                                    <div className="text-4xl mb-2">🖼️</div>
                                    <p className="text-gray-300 mb-1">
                                        {coverFile ? coverFile.name : 'Click to upload cover'}
                                    </p>
                                    <p className="text-gray-500 text-sm">
                                        JPG, PNG (max 5MB)
                                    </p>
                                </div>
                            </div>

                            {/* Przyciski */}
                            <div className="flex gap-4 pt-4">
                                <button
                                    type="button"
                                    onClick={resetForm}
                                    className="px-6 py-3 bg-gray-700 hover:bg-gray-600 rounded-lg transition flex-1"
                                    disabled={loading}
                                >
                                    Reset
                                </button>
                                <button
                                    type="submit"
                                    disabled={loading}
                                    className="px-6 py-3 bg-gradient-to-r from-purple-700 to-pink-700 hover:from-purple-600 hover:to-pink-600 rounded-lg transition flex-1 disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
                                >
                                    {loading ? (
                                        <>
                                            <div className="animate-spin rounded-full h-5 w-5 border-2 border-white border-t-transparent"></div>
                                            Uploading...
                                        </>
                                    ) : (
                                        'Upload Track'
                                    )}
                                </button>
                            </div>
                        </form>
                    </div>

                    {/* Prawa kolumna - Podgląd */}
                    <div className="bg-gray-800/50 backdrop-blur-sm rounded-2xl p-6">
                        <h2 className="text-xl font-bold mb-6">Track Preview</h2>
                        
                        {/* Karta podglądu */}
                        <div className="bg-gray-900/70 rounded-xl p-6 mb-6">
                            {/* Okładka */}
                            <div className="mb-6">
                                <div className="aspect-square rounded-lg overflow-hidden bg-gradient-to-br from-purple-900/30 to-pink-900/30 flex items-center justify-center">
                                    {coverPreview ? (
                                        <img 
                                            src={coverPreview} 
                                            alt="Cover" 
                                            className="w-full h-full object-cover"
                                        />
                                    ) : (
                                        <div className="text-6xl">🎵</div>
                                    )}
                                </div>
                            </div>
                            
                            {/* Informacje o tracku */}
                            <div className="space-y-4">
                                <div>
                                    <h3 className="text-2xl font-bold">
                                        {title || 'Untitled Track'}
                                    </h3>
                                    <p className="text-gray-400">
                                        {artist || 'Unknown Artist'}
                                    </p>
                                </div>
                                
                                {album && (
                                    <div>
                                        <p className="text-sm text-gray-500">Album</p>
                                        <p className="text-gray-300">{album}</p>
                                    </div>
                                )}
                                
                                {genre && (
                                    <div>
                                        <p className="text-sm text-gray-500">Genre</p>
                                        <span className="inline-block px-3 py-1 bg-purple-900/50 text-purple-300 rounded-full text-sm">
                                            {genre}
                                        </span>
                                    </div>
                                )}
                                
                                {duration > 0 && (
                                    <div>
                                        <p className="text-sm text-gray-500">Duration</p>
                                        <p className="text-gray-300">{formatDuration(duration)}</p>
                                    </div>
                                )}
                                
                                {description && (
                                    <div>
                                        <p className="text-sm text-gray-500">Description</p>
                                        <p className="text-gray-300">{description}</p>
                                    </div>
                                )}
                            </div>
                        </div>
                        
                        {/* Odtwarzacz audio */}
                        {audioPreview && (
                            <div className="bg-gray-900/70 rounded-xl p-6">
                                <h3 className="font-bold mb-4">Audio Preview</h3>
                                <audio 
                                    controls 
                                    className="w-full"
                                    src={audioPreview}
                                >
                                    Your browser does not support the audio element.
                                </audio>
                                <p className="text-gray-400 text-sm mt-2 text-center">
                                    Use this player to preview your track
                                </p>
                            </div>
                        )}
                        
                        {/* Informacje dla artysty */}
                        <div className="mt-6 p-4 bg-purple-900/20 rounded-lg">
                            <h4 className="font-bold text-purple-300 mb-2">💡 Tips for Artists</h4>
                            <ul className="text-gray-400 text-sm space-y-1">
                                <li>• Use high-quality audio files (320kbps MP3 or lossless)</li>
                                <li>• Add a compelling cover image (1400x1400px recommended)</li>
                                <li>• Write descriptive titles and descriptions</li>
                                <li>• Tag your tracks with appropriate genres</li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default Upload;