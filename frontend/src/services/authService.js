import { api }  from './api';
import { userApi } from './api';

const authService = {
    register: (userDetails) => userApi.post('/auth/register', userDetails),
    login: async (credentials) => {
        console.log("Login API called with credentials", credentials);
        const response = await userApi.post('/auth/login', credentials);
        return response.data;
    },
    logout: async (navigate) => {
        try {
            await api.post('/auth/logout');
        } catch (e) {
            console.error("Logout API error", e);
        } finally {
            localStorage.removeItem('accessToken');
            localStorage.removeItem('refreshToken');
        }
        if (navigate) navigate('/login');
    },
    resetPassword: (username) => api.post('/auth/reset-password', { username })
}

export default authService;