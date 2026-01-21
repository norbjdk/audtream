import axios from 'axios';

const API_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor do dodawania tokena do requestów
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Interceptor do obsługi błędów
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
  createTrack: (trackData) => api.post('/tracks', trackData),
  updateTrack: (id, trackData) => api.put(`/tracks/${id}`, trackData),
  deleteTrack: (id) => api.delete(`/tracks/${id}`),
};

export default api;