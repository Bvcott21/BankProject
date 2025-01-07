import api from './api';

const authService = {
    register: (userDetails) => api.post('/auth/register', userDetails),
    login: (credentials) => api.post('/auth/login', credentials),
    logout: () => api.post('/auth/logout'),
    resetPassword: (username) => api.post('/auth/reset-password', { username })
}

export default authService;