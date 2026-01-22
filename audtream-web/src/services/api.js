import axios from 'axios';

const API_URL = 'http://localhost:8080/api';

const api = axios.create({
    baseURL: API_URL,
});

api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }

        if (!(config.data instanceof FormData)) {
            config.headers['Content-Type'] = 'application/json';
        }

        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401) {
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);

export const authAPI = {
    login: (username, password) =>
        api.post('/auth/login', { username, password }),
    register: (username, email, password, role) =>
        api.post('/auth/register', { username, email, password, role }),
};

export const userAPI = {
    getCurrentUser: () => api.get('/users/me'),
    getUserByUsername: (username) => api.get(`/users/${username}`),
    getUserById: (id) => api.get(`/users/${id}`)
};

export const tracksAPI = {
    getUserTracks: () => api.get('/tracks'),

    // Upload z plikami - FormData
    createTrack: (formData) => api.post('/tracks', formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    }),

    updateTrack: (id, trackData) => api.put(`/tracks/${id}`, trackData),
    deleteTrack: (id) => api.delete(`/tracks/${id}`),
    streamTrack: (id) => api.get(`/tracks/stream/${id}`),
    getTrackUrl: (id) => api.get(`/tracks/${id}/url`),
    incrementPlayCount: (id) => api.post(`/tracks/${id}/play`),
    toggleLike: (id) => api.post(`/tracks/${id}/like`),
};

export default api;