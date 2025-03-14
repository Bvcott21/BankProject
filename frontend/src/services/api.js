import { createAxiosInstance } from './axiosClient';

const userApi = createAxiosInstance(
    process.env.REACT_APP_USER_API_URL || 'http://localhost:8081/api/v1'
);

const accountApi = createAxiosInstance(
    process.env.REACT_APP_ACCOUNT_API_URL || 'http://localhost:8082/api/v1'
);

const api = createAxiosInstance(
    process.env.REACT_APP_API_URL || 'http://localhost:8080/api/v1',
    true // enable interceptors for this instance
);

export { userApi, accountApi, api };