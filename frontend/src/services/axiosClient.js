import axios from 'axios';

export function createAxiosInstance(baseURL, withAuthInterceptors = false) {
    const instance = axios.create({
        baseURL,
        headers: {
            'Content-Type': 'application/json',
        },
    });

    if (withAuthInterceptors) {
        instance.interceptors.request.use(
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

        instance.interceptors.response.use(
            (response) => response,
            async (error) => {
                if (error.response && error.response.status === 401) {
                    const refreshToken = localStorage.getItem('refreshToken');
                    if (refreshToken) {
                        try {
                            const refreshResponse = await axios.post(
                                `${baseURL}/auth/refresh-token`,
                                { refreshToken }
                            );
                            const newAccessToken = refreshResponse.data.accessToken;
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
    }
    return instance;
}