import { createAxiosInstance } from './axiosClient';

const userApi = createAxiosInstance(
    process.env.REACT_APP_USER_API_URL || 'http://localhost:8081/api/v1'
);

const api = createAxiosInstance(
    process.env.REACT_APP_API_URL || 'http://localhost:8080/api/v1',
    true // enable interceptors for this instance
);

export { userApi, api };