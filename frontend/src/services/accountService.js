import api from "./api";

export const fetchAccounts = async () => {
    try {
        const response = await api.get("/accounts");
        return response.data;
    } catch(error) {
        console.error("Error fetching accounts: ", error);
        throw error;
    }
};

export const createAccountRequest = async (accountRequestData) => {
    try {
        const response = await api.post("/accounts/create-request", accountRequestData);
        return response.data;
    } catch(error) {
        console.error("Error creating account: ", error);
        throw error;
    }
};

export const fetchAccountCreationRequests = async () => {
    try {
        const response = await api.get("/account-requests");
        return response.data;
    } catch(error) {
        console.error("Error fetching account creation requests: ", error);
        throw error;
    }
}

export const fetchAccountCreationRequestById = async (requestId) => {
    try {
        const response = await api.get(`/account-requests/${requestId}`);
        return response.data;
    } catch(error) {
        console.error("Error fetching account creation request: ", error);
        throw error;
    }
}

export const addCommentToAccountCreationRequest = async (requestId, formData) => {
    try {
        const response = await api.post(`/account-requests/comment/${requestId}`, formData);
        return response.data;
    } catch(error) {
        console.error("Error adding comment: ", error);
        throw error;
    }
}

export const updateAccountCreationRequestStatus = async (requestId, formData) => {
    try {
        const response = await api.post(`/account-requests/${requestId}`, formData);
        return response.data;
    } catch(error) {
        console.error("Error updating account creation status: ", error);
        throw error;
    }
}