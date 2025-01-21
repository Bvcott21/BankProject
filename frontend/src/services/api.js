import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8080/api/v1',
    headers: {
        'Content-Type': 'application/json',
    },
});

api.interceptors.request.use(
    async (config) => {
        const accessToken = localStorage.getItem('accessToken');
        console.log("Interceptor: Access Token: ", accessToken);
        if (accessToken) {
            config.headers.Authorization = accessToken;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

api.interceptors.response.use(
    (response) => response,
    async (error) => {
        if (error.response && error.response.status === 401) {
            const refreshToken = localStorage.getItem('refreshToken');
            if (refreshToken) {
                try {
                    const response = await axios.post('http://localhost:8080/api/v1/auth/refresh-token', {
                        refreshToken,
                    });
                    const newAccessToken = response.data.accessToken;
                    localStorage.setItem('accessToken', newAccessToken);
                    error.config.headers.Authorization = newAccessToken;
                    return axios.request(error.config);
                } catch (refreshError) {
                    console.error('Refresh token expired or invalid:', refreshError);
                    localStorage.removeItem('accessToken');
                    localStorage.removeItem('refreshToken');
                    window.location.href = '/login';
                }
            } else {
                window.location.href = '/login';
            }
        }
        return Promise.reject(error);
    }
);

export default api;