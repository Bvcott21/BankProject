import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8080/api/v1',
    headers: {
        'Content-Type': 'application/json',
    },
});

// Adding a request interceptor to include token
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token'); // Retrieve the token from localStorage
        console.log("Token in localStorage: ", token);
        console.log("Request URL: ", config.url);
        console.log("Request Headers: ", config.headers);
        if (token) {
            config.headers.Authorization = `${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response && error.response.status === 401) {
            console.error('Unauthorized: Redirecting to login.');
            // Redirect to login page or show a login prompt
            window.location.href = '/login'; // Adjust path as needed
        }
        console.error('API error:', error.response || error.message);
        return Promise.reject(error);
    }
);

export default api;