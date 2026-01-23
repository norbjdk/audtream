import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

function Settings() {
    const { user, logout } = useAuth();
    const navigate = useNavigate();

    const [settings, setSettings] = useState({
        emailNotifications: true,
        pushNotifications: false,
        newsletter: true,
        publicProfile: true,
        autoPlay: false,
        audioQuality: 'high',
        theme: 'dark',
    });

    const [loading, setLoading] = useState(false);
    const [saved, setSaved] = useState(false);

    // Form states
    const [showEmailModal, setShowEmailModal] = useState(false);
    const [showPasswordModal, setShowPasswordModal] = useState(false);
    const [newEmail, setNewEmail] = useState('');
    const [currentPassword, setCurrentPassword] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');

    // Load saved settings from localStorage
    useEffect(() => {
        const savedSettings = localStorage.getItem('userSettings');
        if (savedSettings) {
            try {
                const parsed = JSON.parse(savedSettings);
                setSettings(parsed);

                // Apply saved theme
                if (parsed.theme) {
                    applyTheme(parsed.theme);
                }
            } catch (e) {
                console.error('Error loading settings:', e);
            }
        }
    }, []);

    // Theme switching function
    const applyTheme = (theme) => {
        const html = document.documentElement;

        if (theme === 'light') {
            html.style.backgroundColor = '#ffffff';
            html.style.color = '#111827';
            document.body.style.backgroundColor = '#ffffff';
            document.body.style.color = '#111827';
        } else {
            html.style.backgroundColor = '#111827';
            html.style.color = '#ffffff';
            document.body.style.backgroundColor = '#111827';
            document.body.style.color = '#ffffff';
        }

        // Save theme in localStorage
        localStorage.setItem('theme', theme);
    };

    const handleSettingChange = (key, value) => {
        const newSettings = {
            ...settings,
            [key]: value
        };
        setSettings(newSettings);

        // Apply theme immediately
        if (key === 'theme') {
            applyTheme(value);
        }

        // Save to localStorage
        localStorage.setItem('userSettings', JSON.stringify(newSettings));
    };

    const saveSettings = () => {
        setLoading(true);
        setTimeout(() => {
            localStorage.setItem('userSettings', JSON.stringify(settings));
            setSaved(true);
            setLoading(false);
            setTimeout(() => setSaved(false), 3000);
        }, 500);
    };

    const handleChangeEmail = async () => {
        if (!newEmail.trim()) {
            alert('Please enter a valid email address');
            return;
        }

        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(newEmail)) {
            alert('Please enter a valid email address');
            return;
        }

        setLoading(true);
        try {
            await new Promise(resolve => setTimeout(resolve, 1000));
            alert(`Email changed to ${newEmail}`);
            setShowEmailModal(false);
            setNewEmail('');
        } catch (error) {
            alert('Failed to change email. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    const handleChangePassword = async () => {
        if (!currentPassword) {
            alert('Please enter your current password');
            return;
        }

        if (!newPassword) {
            alert('Please enter a new password');
            return;
        }

        if (newPassword.length < 6) {
            alert('Password must be at least 6 characters long');
            return;
        }

        if (newPassword !== confirmPassword) {
            alert('New passwords do not match');
            return;
        }

        setLoading(true);
        try {
            await new Promise(resolve => setTimeout(resolve, 1000));
            alert('Password changed successfully!');
            setShowPasswordModal(false);
            setCurrentPassword('');
            setNewPassword('');
            setConfirmPassword('');
        } catch (error) {
            alert('Failed to change password. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    const handleLogout = async () => {
        if (window.confirm('Are you sure you want to logout?')) {
            await logout();
            navigate('/login');
        }
    };

    const handleDeleteAccount = () => {
        if (window.confirm('Are you sure? This action cannot be undone. All your tracks and data will be permanently deleted.')) {
            const password = prompt('Please enter your password to confirm account deletion:');
            if (password) {
                setLoading(true);
                setTimeout(() => {
                    alert('Account deletion requested. You will receive a confirmation email.');
                    setLoading(false);
                }, 1500);
            }
        }
    };

    // Determine background and text colors based on theme
    const getThemeStyles = () => {
        const isDark = settings.theme === 'dark';
        return {
            backgroundColor: isDark ? '#111827' : '#ffffff',
            color: isDark ? '#ffffff' : '#111827',
            cardBackground: isDark ? 'rgba(255, 255, 255, 0.05)' : 'rgba(0, 0, 0, 0.03)',
            borderColor: isDark ? 'rgba(255, 255, 255, 0.1)' : 'rgba(0, 0, 0, 0.1)',
            textMuted: isDark ? '#9ca3af' : '#6b7280'
        };
    };

    const themeStyles = getThemeStyles();

    return (
        <div style={{
            minHeight: '100vh',
            backgroundColor: themeStyles.backgroundColor,
            color: themeStyles.color,
            padding: '1rem',
            transition: 'all 0.3s ease'
        }}>
            <div style={{ maxWidth: '64rem', margin: '0 auto' }}>
                {/* Header */}
                <div style={{ marginBottom: '2rem' }}>
                    <div style={{
                        display: 'flex',
                        flexDirection: 'column',
                        justifyContent: 'space-between',
                        alignItems: 'flex-start',
                        gap: '1rem',
                        marginBottom: '1.5rem'
                    }}>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                            <div style={{
                                width: '2.5rem',
                                height: '2.5rem',
                                background: 'linear-gradient(135deg, #3b82f6 0%, #06b6d4 100%)',
                                borderRadius: '0.75rem',
                                display: 'flex',
                                alignItems: 'center',
                                justifyContent: 'center'
                            }}>
                                <span style={{ fontSize: '1.25rem' }}>⚙️</span>
                            </div>
                            <div>
                                <h1 style={{ fontSize: '1.875rem', fontWeight: 'bold', margin: 0 }}>Settings</h1>
                                <p style={{ color: themeStyles.textMuted, margin: 0 }}>Manage your account preferences</p>
                            </div>
                        </div>

                        {saved && (
                            <div style={{
                                padding: '0.5rem 1rem',
                                background: 'rgba(34, 197, 94, 0.2)',
                                border: '1px solid rgba(34, 197, 94, 0.3)',
                                borderRadius: '0.5rem',
                                color: '#10b981'
                            }}>
                                <span>✓ Settings saved successfully</span>
                            </div>
                        )}
                    </div>
                </div>

                {/* Settings Sections */}
                <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
                    {/* Theme Settings */}
                    <div style={{
                        backgroundColor: themeStyles.cardBackground,
                        borderRadius: '1rem',
                        padding: '1.5rem',
                        border: `1px solid ${themeStyles.borderColor}`
                    }}>
                        <h2 style={{
                            fontSize: '1.25rem',
                            fontWeight: 'bold',
                            marginBottom: '1.5rem',
                            display: 'flex',
                            alignItems: 'center',
                            gap: '0.5rem'
                        }}>
                            <span style={{ color: '#8b5cf6' }}>🎨</span>
                            Appearance
                        </h2>

                        <div style={{
                            padding: '1rem',
                            backgroundColor: settings.theme === 'dark' ? 'rgba(0, 0, 0, 0.2)' : 'rgba(243, 244, 246, 0.5)',
                            borderRadius: '0.75rem'
                        }}>
                            <div style={{ marginBottom: '0.75rem' }}>
                                <h3 style={{ fontWeight: '600', marginBottom: '0.25rem' }}>Theme</h3>
                                <p style={{ color: themeStyles.textMuted, fontSize: '0.875rem' }}>
                                    Choose your preferred interface style
                                </p>
                            </div>
                            <div style={{
                                display: 'grid',
                                gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
                                gap: '1rem',
                                margin: '1rem 0'
                            }}>
                                {[
                                    {
                                        value: 'light',
                                        label: 'Light',
                                        icon: '☀️',
                                        description: 'Bright mode'
                                    },
                                    {
                                        value: 'dark',
                                        label: 'Dark',
                                        icon: '🌙',
                                        description: 'Dark mode'
                                    }
                                ].map((themeOption) => (
                                    <button
                                        key={themeOption.value}
                                        onClick={() => handleSettingChange('theme', themeOption.value)}
                                        style={{
                                            display: 'flex',
                                            flexDirection: 'column',
                                            alignItems: 'center',
                                            padding: '1.5rem',
                                            borderRadius: '0.75rem',
                                            transition: 'all 0.3s ease',
                                            cursor: 'pointer',
                                            background: settings.theme === themeOption.value
                                                ? 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
                                                : themeOption.value === 'light' ? '#f3f4f6' : '#1f2937',
                                            color: settings.theme === themeOption.value
                                                ? 'white'
                                                : themeOption.value === 'light' ? '#374151' : '#d1d5db',
                                            border: `2px solid ${settings.theme === themeOption.value ? '#667eea' :
                                                themeOption.value === 'light' ? '#d1d5db' : '#374151'}`,
                                            boxShadow: settings.theme === themeOption.value ? '0 4px 20px rgba(102, 126, 234, 0.3)' : 'none',
                                            transform: settings.theme === themeOption.value ? 'scale(1.02)' : 'scale(1)'
                                        }}
                                        onMouseOver={(e) => {
                                            if (settings.theme !== themeOption.value) {
                                                e.currentTarget.style.transform = 'scale(1.01)';
                                            }
                                        }}
                                        onMouseOut={(e) => {
                                            if (settings.theme !== themeOption.value) {
                                                e.currentTarget.style.transform = 'scale(1)';
                                            }
                                        }}
                                    >
                                        <span style={{ fontSize: '1.5rem', marginBottom: '0.5rem' }}>{themeOption.icon}</span>
                                        <span style={{ fontWeight: '600', marginBottom: '0.25rem' }}>{themeOption.label}</span>
                                        <span style={{ fontSize: '0.875rem', opacity: 0.8 }}>{themeOption.description}</span>
                                        {settings.theme === themeOption.value && (
                                            <div style={{
                                                marginTop: '0.5rem',
                                                padding: '0.25rem 0.75rem',
                                                background: settings.theme === 'dark' ? 'rgba(59, 130, 246, 0.5)' : 'rgba(59, 130, 246, 0.2)',
                                                color: settings.theme === 'dark' ? '#93c5fd' : '#3b82f6',
                                                borderRadius: '9999px',
                                                fontSize: '0.75rem'
                                            }}>
                                                Active
                                            </div>
                                        )}
                                    </button>
                                ))}
                            </div>

                            <div style={{
                                padding: '0.75rem',
                                background: settings.theme === 'dark' ? 'rgba(59, 130, 246, 0.1)' : 'rgba(191, 219, 254, 0.3)',
                                borderRadius: '0.5rem',
                                marginTop: '1rem'
                            }}>
                                <p style={{
                                    fontSize: '0.875rem',
                                    color: settings.theme === 'dark' ? '#93c5fd' : '#3b82f6'
                                }}>
                                    <span style={{ fontWeight: 'bold' }}>Current theme:</span> {settings.theme === 'light' ? 'Light mode' : 'Dark mode'}
                                </p>
                                <p style={{
                                    fontSize: '0.75rem',
                                    opacity: 0.7,
                                    marginTop: '0.25rem',
                                    color: settings.theme === 'dark' ? '#93c5fd' : '#3b82f6'
                                }}>
                                    Changes apply immediately
                                </p>
                            </div>
                        </div>
                    </div>

                    {/* Account Settings */}
                    <div style={{
                        backgroundColor: themeStyles.cardBackground,
                        borderRadius: '1rem',
                        padding: '1.5rem',
                        border: `1px solid ${themeStyles.borderColor}`
                    }}>
                        <h2 style={{
                            fontSize: '1.25rem',
                            fontWeight: 'bold',
                            marginBottom: '1.5rem',
                            display: 'flex',
                            alignItems: 'center',
                            gap: '0.5rem'
                        }}>
                            <span style={{ color: '#3b82f6' }}>👤</span>
                            Account Settings
                        </h2>

                        <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                            <div style={{
                                display: 'flex',
                                flexDirection: 'column',
                                justifyContent: 'space-between',
                                alignItems: 'flex-start',
                                gap: '0.75rem',
                                padding: '1rem',
                                backgroundColor: settings.theme === 'dark' ? 'rgba(0, 0, 0, 0.2)' : 'rgba(243, 244, 246, 0.5)',
                                borderRadius: '0.5rem'
                            }}>
                                <div>
                                    <h3 style={{ fontWeight: '600' }}>Email Address</h3>
                                    <p style={{ color: themeStyles.textMuted, fontSize: '0.875rem' }}>
                                        {user?.email || 'user@example.com'}
                                    </p>
                                </div>
                                <button
                                    onClick={() => setShowEmailModal(true)}
                                    style={{
                                        padding: '0.5rem 1rem',
                                        backgroundColor: settings.theme === 'dark' ? '#374151' : '#e5e7eb',
                                        color: settings.theme === 'dark' ? '#d1d5db' : '#374151',
                                        border: 'none',
                                        borderRadius: '0.375rem',
                                        cursor: 'pointer',
                                        fontSize: '0.875rem',
                                        transition: 'background-color 0.2s'
                                    }}
                                    onMouseOver={(e) => {
                                        e.currentTarget.style.backgroundColor = settings.theme === 'dark' ? '#4b5563' : '#d1d5db';
                                    }}
                                    onMouseOut={(e) => {
                                        e.currentTarget.style.backgroundColor = settings.theme === 'dark' ? '#374151' : '#e5e7eb';
                                    }}
                                >
                                    Change Email
                                </button>
                            </div>

                            <div style={{
                                display: 'flex',
                                flexDirection: 'column',
                                justifyContent: 'space-between',
                                alignItems: 'flex-start',
                                gap: '0.75rem',
                                padding: '1rem',
                                backgroundColor: settings.theme === 'dark' ? 'rgba(0, 0, 0, 0.2)' : 'rgba(243, 244, 246, 0.5)',
                                borderRadius: '0.5rem'
                            }}>
                                <div>
                                    <h3 style={{ fontWeight: '600' }}>Password</h3>
                                    <p style={{ color: themeStyles.textMuted, fontSize: '0.875rem' }}>
                                        ●●●●●●●●
                                    </p>
                                </div>
                                <button
                                    onClick={() => setShowPasswordModal(true)}
                                    style={{
                                        padding: '0.5rem 1rem',
                                        backgroundColor: settings.theme === 'dark' ? '#374151' : '#e5e7eb',
                                        color: settings.theme === 'dark' ? '#d1d5db' : '#374151',
                                        border: 'none',
                                        borderRadius: '0.375rem',
                                        cursor: 'pointer',
                                        fontSize: '0.875rem',
                                        transition: 'background-color 0.2s'
                                    }}
                                    onMouseOver={(e) => {
                                        e.currentTarget.style.backgroundColor = settings.theme === 'dark' ? '#4b5563' : '#d1d5db';
                                    }}
                                    onMouseOut={(e) => {
                                        e.currentTarget.style.backgroundColor = settings.theme === 'dark' ? '#374151' : '#e5e7eb';
                                    }}
                                >
                                    Change Password
                                </button>
                            </div>
                        </div>
                    </div>

                    {/* Notification Settings */}
                    <div style={{
                        backgroundColor: themeStyles.cardBackground,
                        borderRadius: '1rem',
                        padding: '1.5rem',
                        border: `1px solid ${themeStyles.borderColor}`
                    }}>
                        <h2 style={{
                            fontSize: '1.25rem',
                            fontWeight: 'bold',
                            marginBottom: '1.5rem',
                            display: 'flex',
                            alignItems: 'center',
                            gap: '0.5rem'
                        }}>
                            <span style={{ color: '#f59e0b' }}>🔔</span>
                            Notifications
                        </h2>

                        <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                            {[
                                { key: 'emailNotifications', label: 'Email Notifications', description: 'Receive updates via email' },
                                { key: 'pushNotifications', label: 'Push Notifications', description: 'Browser push notifications' },
                                { key: 'newsletter', label: 'Newsletter', description: 'Weekly music recommendations' }
                            ].map((item) => (
                                <div key={item.key} style={{
                                    display: 'flex',
                                    alignItems: 'center',
                                    justifyContent: 'space-between',
                                    padding: '1rem',
                                    backgroundColor: settings.theme === 'dark' ? 'rgba(0, 0, 0, 0.2)' : 'rgba(243, 244, 246, 0.5)',
                                    borderRadius: '0.5rem'
                                }}>
                                    <div>
                                        <h3 style={{ fontWeight: '600' }}>{item.label}</h3>
                                        <p style={{ color: themeStyles.textMuted, fontSize: '0.875rem' }}>{item.description}</p>
                                    </div>
                                    <label style={{ position: 'relative', display: 'inline-flex', alignItems: 'center', cursor: 'pointer' }}>
                                        <input
                                            type="checkbox"
                                            checked={settings[item.key]}
                                            onChange={(e) => handleSettingChange(item.key, e.target.checked)}
                                            style={{ display: 'none' }}
                                        />
                                        <div style={{
                                            width: '2.75rem',
                                            height: '1.5rem',
                                            backgroundColor: settings[item.key] ? '#3b82f6' : (settings.theme === 'dark' ? '#4b5563' : '#d1d5db'),
                                            borderRadius: '9999px',
                                            position: 'relative',
                                            transition: 'background-color 0.2s'
                                        }}>
                                            <div style={{
                                                position: 'absolute',
                                                top: '2px',
                                                left: settings[item.key] ? 'calc(100% - 1.25rem - 2px)' : '2px',
                                                width: '1.25rem',
                                                height: '1.25rem',
                                                backgroundColor: 'white',
                                                borderRadius: '50%',
                                                transition: 'left 0.2s',
                                                boxShadow: '0 1px 3px rgba(0,0,0,0.2)'
                                            }} />
                                        </div>
                                    </label>
                                </div>
                            ))}
                        </div>
                    </div>

                    {/* Audio Settings */}
                    <div style={{
                        backgroundColor: themeStyles.cardBackground,
                        borderRadius: '1rem',
                        padding: '1.5rem',
                        border: `1px solid ${themeStyles.borderColor}`
                    }}>
                        <h2 style={{
                            fontSize: '1.25rem',
                            fontWeight: 'bold',
                            marginBottom: '1.5rem',
                            display: 'flex',
                            alignItems: 'center',
                            gap: '0.5rem'
                        }}>
                            <span style={{ color: '#10b981' }}>🎵</span>
                            Audio Settings
                        </h2>

                        <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                            <div style={{
                                padding: '1rem',
                                backgroundColor: settings.theme === 'dark' ? 'rgba(0, 0, 0, 0.2)' : 'rgba(243, 244, 246, 0.5)',
                                borderRadius: '0.5rem'
                            }}>
                                <div style={{ marginBottom: '0.75rem' }}>
                                    <h3 style={{ fontWeight: '600', marginBottom: '0.25rem' }}>Audio Quality</h3>
                                    <p style={{ color: themeStyles.textMuted, fontSize: '0.875rem' }}>
                                        Streaming quality preference
                                    </p>
                                </div>
                                <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem' }}>
                                    {['low', 'medium', 'high'].map((quality) => (
                                        <button
                                            key={quality}
                                            onClick={() => handleSettingChange('audioQuality', quality)}
                                            style={{
                                                padding: '0.5rem 1rem',
                                                border: 'none',
                                                borderRadius: '0.375rem',
                                                cursor: 'pointer',
                                                transition: 'all 0.2s',
                                                backgroundColor: settings.audioQuality === quality
                                                    ? (settings.theme === 'dark' ? '#3b82f6' : '#2563eb')
                                                    : (settings.theme === 'dark' ? '#374151' : '#e5e7eb'),
                                                color: settings.audioQuality === quality
                                                    ? 'white'
                                                    : (settings.theme === 'dark' ? '#d1d5db' : '#374151')
                                            }}
                                            onMouseOver={(e) => {
                                                if (settings.audioQuality !== quality) {
                                                    e.currentTarget.style.backgroundColor = settings.theme === 'dark' ? '#4b5563' : '#d1d5db';
                                                }
                                            }}
                                            onMouseOut={(e) => {
                                                if (settings.audioQuality !== quality) {
                                                    e.currentTarget.style.backgroundColor = settings.theme === 'dark' ? '#374151' : '#e5e7eb';
                                                }
                                            }}
                                        >
                                            {quality.charAt(0).toUpperCase() + quality.slice(1)}
                                        </button>
                                    ))}
                                </div>
                            </div>

                            <div style={{
                                display: 'flex',
                                alignItems: 'center',
                                justifyContent: 'space-between',
                                padding: '1rem',
                                backgroundColor: settings.theme === 'dark' ? 'rgba(0, 0, 0, 0.2)' : 'rgba(243, 244, 246, 0.5)',
                                borderRadius: '0.5rem'
                            }}>
                                <div>
                                    <h3 style={{ fontWeight: '600' }}>Auto-play</h3>
                                    <p style={{ color: themeStyles.textMuted, fontSize: '0.875rem' }}>
                                        Automatically play next track
                                    </p>
                                </div>
                                <label style={{ position: 'relative', display: 'inline-flex', alignItems: 'center', cursor: 'pointer' }}>
                                    <input
                                        type="checkbox"
                                        checked={settings.autoPlay}
                                        onChange={(e) => handleSettingChange('autoPlay', e.target.checked)}
                                        style={{ display: 'none' }}
                                    />
                                    <div style={{
                                        width: '2.75rem',
                                        height: '1.5rem',
                                        backgroundColor: settings.autoPlay ? '#3b82f6' : (settings.theme === 'dark' ? '#4b5563' : '#d1d5db'),
                                        borderRadius: '9999px',
                                        position: 'relative',
                                        transition: 'background-color 0.2s'
                                    }}>
                                        <div style={{
                                            position: 'absolute',
                                            top: '2px',
                                            left: settings.autoPlay ? 'calc(100% - 1.25rem - 2px)' : '2px',
                                            width: '1.25rem',
                                            height: '1.25rem',
                                            backgroundColor: 'white',
                                            borderRadius: '50%',
                                            transition: 'left 0.2s',
                                            boxShadow: '0 1px 3px rgba(0,0,0,0.2)'
                                        }} />
                                    </div>
                                </label>
                            </div>
                        </div>
                    </div>

                    {/* Danger Zone */}
                    <div style={{
                        backgroundColor: settings.theme === 'dark' ? 'rgba(127, 29, 29, 0.1)' : 'rgba(254, 226, 226, 0.3)',
                        borderRadius: '1rem',
                        padding: '1.5rem',
                        border: '1px solid rgba(220, 38, 38, 0.3)'
                    }}>
                        <h2 style={{
                            fontSize: '1.25rem',
                            fontWeight: 'bold',
                            marginBottom: '1.5rem',
                            display: 'flex',
                            alignItems: 'center',
                            gap: '0.5rem',
                            color: '#ef4444'
                        }}>
                            <span>⚠️</span>
                            Danger Zone
                        </h2>

                        <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                            <div style={{
                                display: 'flex',
                                flexDirection: 'column',
                                justifyContent: 'space-between',
                                alignItems: 'flex-start',
                                gap: '0.75rem',
                                padding: '1rem',
                                backgroundColor: settings.theme === 'dark' ? 'rgba(127, 29, 29, 0.2)' : 'rgba(254, 226, 226, 0.5)',
                                borderRadius: '0.5rem'
                            }}>
                                <div>
                                    <h3 style={{ fontWeight: '600', color: '#fca5a5' }}>Logout</h3>
                                    <p style={{ color: settings.theme === 'dark' ? '#fca5a5' : '#dc2626', fontSize: '0.875rem' }}>
                                        Sign out from all devices
                                    </p>
                                </div>
                                <button
                                    onClick={handleLogout}
                                    style={{
                                        padding: '0.5rem 1rem',
                                        backgroundColor: '#dc2626',
                                        color: 'white',
                                        border: 'none',
                                        borderRadius: '0.375rem',
                                        cursor: 'pointer',
                                        fontSize: '0.875rem',
                                        transition: 'background-color 0.2s'
                                    }}
                                    onMouseOver={(e) => {
                                        e.currentTarget.style.backgroundColor = '#b91c1c';
                                    }}
                                    onMouseOut={(e) => {
                                        e.currentTarget.style.backgroundColor = '#dc2626';
                                    }}
                                >
                                    Logout
                                </button>
                            </div>

                            <div style={{
                                display: 'flex',
                                flexDirection: 'column',
                                justifyContent: 'space-between',
                                alignItems: 'flex-start',
                                gap: '0.75rem',
                                padding: '1rem',
                                backgroundColor: settings.theme === 'dark' ? 'rgba(127, 29, 29, 0.2)' : 'rgba(254, 226, 226, 0.5)',
                                borderRadius: '0.5rem'
                            }}>
                                <div>
                                    <h3 style={{ fontWeight: '600', color: '#fca5a5' }}>Delete Account</h3>
                                    <p style={{ color: settings.theme === 'dark' ? '#fca5a5' : '#dc2626', fontSize: '0.875rem' }}>
                                        Permanently delete your account and all data
                                    </p>
                                </div>
                                <button
                                    onClick={handleDeleteAccount}
                                    style={{
                                        padding: '0.5rem 1rem',
                                        backgroundColor: '#7f1d1d',
                                        color: '#fca5a5',
                                        border: '1px solid #dc2626',
                                        borderRadius: '0.375rem',
                                        cursor: 'pointer',
                                        fontSize: '0.875rem',
                                        transition: 'background-color 0.2s'
                                    }}
                                    onMouseOver={(e) => {
                                        e.currentTarget.style.backgroundColor = '#991b1b';
                                    }}
                                    onMouseOut={(e) => {
                                        e.currentTarget.style.backgroundColor = '#7f1d1d';
                                    }}
                                >
                                    Delete Account
                                </button>
                            </div>
                        </div>
                    </div>

                    {/* Save Button */}
                    <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '1rem' }}>
                        <button
                            onClick={() => navigate('/')}
                            style={{
                                padding: '0.75rem 1.5rem',
                                backgroundColor: settings.theme === 'dark' ? '#374151' : '#e5e7eb',
                                color: settings.theme === 'dark' ? '#d1d5db' : '#374151',
                                border: 'none',
                                borderRadius: '0.5rem',
                                cursor: 'pointer',
                                fontWeight: '500',
                                transition: 'background-color 0.2s'
                            }}
                            onMouseOver={(e) => {
                                e.currentTarget.style.backgroundColor = settings.theme === 'dark' ? '#4b5563' : '#d1d5db';
                            }}
                            onMouseOut={(e) => {
                                e.currentTarget.style.backgroundColor = settings.theme === 'dark' ? '#374151' : '#e5e7eb';
                            }}
                        >
                            Cancel
                        </button>
                        <button
                            onClick={saveSettings}
                            disabled={loading}
                            style={{
                                padding: '0.75rem 1.5rem',
                                background: 'linear-gradient(135deg, #3b82f6 0%, #06b6d4 100%)',
                                color: 'white',
                                border: 'none',
                                borderRadius: '0.5rem',
                                cursor: loading ? 'not-allowed' : 'pointer',
                                fontWeight: '500',
                                opacity: loading ? 0.5 : 1,
                                transition: 'opacity 0.2s'
                            }}
                        >
                            {loading ? 'Saving...' : 'Save Changes'}
                        </button>
                    </div>
                </div>

                {/* Modals */}
                {showEmailModal && (
                    <div style={{
                        position: 'fixed',
                        top: 0,
                        left: 0,
                        right: 0,
                        bottom: 0,
                        backgroundColor: 'rgba(0, 0, 0, 0.7)',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        zIndex: 50,
                        padding: '1rem'
                    }}>
                        <div style={{
                            backgroundColor: settings.theme === 'dark' ? '#1f2937' : 'white',
                            borderRadius: '1rem',
                            padding: '1.5rem',
                            maxWidth: '28rem',
                            width: '100%'
                        }}>
                            <h3 style={{ fontSize: '1.25rem', fontWeight: 'bold', marginBottom: '1rem', color: themeStyles.color }}>
                                Change Email Address
                            </h3>
                            <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                                <div>
                                    <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: '500', marginBottom: '0.5rem', color: themeStyles.textMuted }}>
                                        Current Email
                                    </label>
                                    <input
                                        type="email"
                                        value={user?.email || ''}
                                        disabled
                                        style={{
                                            width: '100%',
                                            padding: '0.5rem 1rem',
                                            backgroundColor: settings.theme === 'dark' ? '#374151' : '#f3f4f6',
                                            color: settings.theme === 'dark' ? '#9ca3af' : '#6b7280',
                                            border: 'none',
                                            borderRadius: '0.375rem'
                                        }}
                                    />
                                </div>
                                <div>
                                    <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: '500', marginBottom: '0.5rem', color: themeStyles.textMuted }}>
                                        New Email
                                    </label>
                                    <input
                                        type="email"
                                        value={newEmail}
                                        onChange={(e) => setNewEmail(e.target.value)}
                                        placeholder="new@email.com"
                                        style={{
                                            width: '100%',
                                            padding: '0.5rem 1rem',
                                            backgroundColor: settings.theme === 'dark' ? '#374151' : '#f3f4f6',
                                            color: themeStyles.color,
                                            border: 'none',
                                            borderRadius: '0.375rem'
                                        }}
                                    />
                                </div>
                                <div style={{ display: 'flex', gap: '0.75rem', justifyContent: 'flex-end', marginTop: '1.5rem' }}>
                                    <button
                                        onClick={() => setShowEmailModal(false)}
                                        style={{
                                            padding: '0.5rem 1rem',
                                            backgroundColor: settings.theme === 'dark' ? '#374151' : '#e5e7eb',
                                            color: settings.theme === 'dark' ? '#d1d5db' : '#374151',
                                            border: 'none',
                                            borderRadius: '0.375rem',
                                            cursor: 'pointer'
                                        }}
                                    >
                                        Cancel
                                    </button>
                                    <button
                                        onClick={handleChangeEmail}
                                        disabled={loading}
                                        style={{
                                            padding: '0.5rem 1rem',
                                            background: 'linear-gradient(135deg, #3b82f6 0%, #06b6d4 100%)',
                                            color: 'white',
                                            border: 'none',
                                            borderRadius: '0.375rem',
                                            cursor: loading ? 'not-allowed' : 'pointer',
                                            opacity: loading ? 0.5 : 1
                                        }}
                                    >
                                        {loading ? 'Changing...' : 'Change Email'}
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                )}

                {showPasswordModal && (
                    <div style={{
                        position: 'fixed',
                        top: 0,
                        left: 0,
                        right: 0,
                        bottom: 0,
                        backgroundColor: 'rgba(0, 0, 0, 0.7)',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        zIndex: 50,
                        padding: '1rem'
                    }}>
                        <div style={{
                            backgroundColor: settings.theme === 'dark' ? '#1f2937' : 'white',
                            borderRadius: '1rem',
                            padding: '1.5rem',
                            maxWidth: '28rem',
                            width: '100%'
                        }}>
                            <h3 style={{ fontSize: '1.25rem', fontWeight: 'bold', marginBottom: '1rem', color: themeStyles.color }}>
                                Change Password
                            </h3>
                            <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                                <div>
                                    <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: '500', marginBottom: '0.5rem', color: themeStyles.textMuted }}>
                                        Current Password
                                    </label>
                                    <input
                                        type="password"
                                        value={currentPassword}
                                        onChange={(e) => setCurrentPassword(e.target.value)}
                                        placeholder="Enter current password"
                                        style={{
                                            width: '100%',
                                            padding: '0.5rem 1rem',
                                            backgroundColor: settings.theme === 'dark' ? '#374151' : '#f3f4f6',
                                            color: themeStyles.color,
                                            border: 'none',
                                            borderRadius: '0.375rem'
                                        }}
                                    />
                                </div>
                                <div>
                                    <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: '500', marginBottom: '0.5rem', color: themeStyles.textMuted }}>
                                        New Password
                                    </label>
                                    <input
                                        type="password"
                                        value={newPassword}
                                        onChange={(e) => setNewPassword(e.target.value)}
                                        placeholder="At least 6 characters"
                                        style={{
                                            width: '100%',
                                            padding: '0.5rem 1rem',
                                            backgroundColor: settings.theme === 'dark' ? '#374151' : '#f3f4f6',
                                            color: themeStyles.color,
                                            border: 'none',
                                            borderRadius: '0.375rem'
                                        }}
                                    />
                                </div>
                                <div>
                                    <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: '500', marginBottom: '0.5rem', color: themeStyles.textMuted }}>
                                        Confirm New Password
                                    </label>
                                    <input
                                        type="password"
                                        value={confirmPassword}
                                        onChange={(e) => setConfirmPassword(e.target.value)}
                                        placeholder="Confirm new password"
                                        style={{
                                            width: '100%',
                                            padding: '0.5rem 1rem',
                                            backgroundColor: settings.theme === 'dark' ? '#374151' : '#f3f4f6',
                                            color: themeStyles.color,
                                            border: 'none',
                                            borderRadius: '0.375rem'
                                        }}
                                    />
                                </div>
                                <div style={{ display: 'flex', gap: '0.75rem', justifyContent: 'flex-end', marginTop: '1.5rem' }}>
                                    <button
                                        onClick={() => setShowPasswordModal(false)}
                                        style={{
                                            padding: '0.5rem 1rem',
                                            backgroundColor: settings.theme === 'dark' ? '#374151' : '#e5e7eb',
                                            color: settings.theme === 'dark' ? '#d1d5db' : '#374151',
                                            border: 'none',
                                            borderRadius: '0.375rem',
                                            cursor: 'pointer'
                                        }}
                                    >
                                        Cancel
                                    </button>
                                    <button
                                        onClick={handleChangePassword}
                                        disabled={loading}
                                        style={{
                                            padding: '0.5rem 1rem',
                                            background: 'linear-gradient(135deg, #3b82f6 0%, #06b6d4 100%)',
                                            color: 'white',
                                            border: 'none',
                                            borderRadius: '0.375rem',
                                            cursor: loading ? 'not-allowed' : 'pointer',
                                            opacity: loading ? 0.5 : 1
                                        }}
                                    >
                                        {loading ? 'Changing...' : 'Change Password'}
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}

export default Settings;